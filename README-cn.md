![license](https://img.shields.io/github/license/dtm-labs/dtm)

## dtm-spring-boot-starter

dtm-spring-boot-starter 是 [dtm](https://github.com/dtm-labs/dtm)的java 客户端，依赖于
spring-boot 2.x版本，目前支持:
+ TCC 分布式事务
+ Saga 分布式事务
+ Msg 两阶段提交
+ 子事务屏障

## 快速开始
1. 引入pom
```xml
<dependency>
    <groupId>com.jsrdxzw.github</groupId>
    <artifactId>dtm-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 配置dtm服务
```yaml
dtm:
  http-server: http://localhost:36789/api/dtmsvr # dtm服务地址

# 配置你项目的sql连接
spring:
   datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      username:
      password:
      url: jdbc:mysql://localhost:3306/{project}
```
3. 数据库执行 [dtm](https://github.com/dtm-labs/dtm) 中的sql文件
   1. dtmcli.barrier.mysql.sql
   2. 如果要使用子事务屏障功能，创建 `barrier` dtmsvr.storage.mysql.sql
4. 项目使用@EnableDtm注解开启自动配置功能
```java
@SpringBootApplication
@EnableDtm
public class DtmSpringBootExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(DtmSpringBootExampleApplication.class, args);
    }
}
```

### Saga

客户端注册Saga全局事务
```java
public void doSomething() {
     TransReq req = new TransReq();
     req.setAmount(BigDecimal.valueOf(100));
     DtmServerResult result = new Saga(httpClient)
                                   .add(HOST + "/SagaBTransOut", HOST + "/SagaBTransOutCom", req)
                                   .add(HOST + "/SagaBTransIn", HOST + "/SagaBTransInCom", req)
                                   .submit(); 
}
```

DM使用

```java
@DtmResponse
@PostMapping("/SagaBTransOut")
public ResultData sagaTransOut(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
   branchBarrier.call(transactionManager, barrier -> {
      userAccountMapper.sagaAdjustBalance(TRANSOUT_UID, transReq.getAmount().negate());
   });
   return ResultData.success();
}

@DtmResponse
@PostMapping("/SagaBTransIn")
public ResultData sagaTransIn(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
   branchBarrier.call(transactionManager, barrier -> {
       userAccountMapper.sagaAdjustBalance(TRANSIN_UID, transReq.getAmount());
   });
   return ResultData.success();
}

@DtmResponse
@PostMapping("/SagaBTransOutCom")
public ResultData sagaTransOutCom(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
   branchBarrier.call(transactionManager, barrier -> {
        userAccountMapper.sagaAdjustBalance(TRANSOUT_UID, transReq.getAmount());
   });
   return ResultData.success();
}

@DtmResponse
@PostMapping("/SagaBTransInCom")
public ResultData sagaTransInCom(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
   branchBarrier.call(transactionManager, barrier -> {
        userAccountMapper.sagaAdjustBalance(2, transReq.getAmount().negate());
   });
   return ResultData.success();
}
```

### Tcc

客户端注册Tcc全局事务
```java
public void doSomething() {
     TransReq transReq = TransReq.builder().amount(BigDecimal.valueOf(30)).build();
     new Tcc(httpClient).tccGlobalTransaction(tcc -> {
        tcc.callBranch(
            transReq, HOST + "/tccTransOutTry", HOST + "/tccTransOutConfirm", HOST + "/tccTransOutCancel");
        tcc.callBranch(
            transReq, HOST + "/tccTransInTry", HOST + "/tccTransInConfirm", HOST + "/tccTransInCancel");
     });
}
```

DM使用
```java
@DtmResponse
@PostMapping("/tccTransInTry")
public ResultData tccTransInTry(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
     branchBarrier.call(transactionManager, barrier -> {
         userAccountMapper.tccAdjustTrading(TRANSIN_UID, transReq.getAmount());
     });
     return ResultData.success();
}
@DtmResponse
@PostMapping("/tccTransInConfirm")
public ResultData tccTransInConfirm(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
   branchBarrier.call(transactionManager, barrier -> {
        userAccountMapper.tccAdjustBalance(TRANSIN_UID, transReq.getAmount());
   });
   return ResultData.success();
}

@DtmResponse
@PostMapping("/tccTransInCancel")
public ResultData tccTransInCancel(@DtmBarrier BranchBarrier branchBarrier, @RequestBody TransReq transReq) throws Exception {
     branchBarrier.call(transactionManager, barrier -> {
        userAccountMapper.tccAdjustTrading(TRANSIN_UID, transReq.getAmount().negate());
     });
     return ResultData.success();
}

// ...
```

### Msg two-phase commit
```java
public void doSomething() {
     TransReq req = TransReq.builder().amount(BigDecimal.valueOf(30)).build();
     Msg msg = new Msg(httpClient).add(HOST + "/SagaBTransOut", req).add(HOST + "/SagaBTransIn", req);
     msg.prepare(HOST + "/query");
     // ... your business logic
     msg.submit();
}
```