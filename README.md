![license](https://img.shields.io/github/license/dtm-labs/dtm)
![Build Status](https://github.com/dtm-labs/dtm/actions/workflows/tests.yml/badge.svg?branch=main)

English | [简体中文](https://github.com/jsrdxzw/dtm-spring-boot-starter/blob/master/README-cn.md)

## dtm-spring-boot-starter

dtm-spring-boot-starter is java sdk based on spring boot for [dtm](https://github.com/dtm-labs/dtm),
spring boot 2.x version is required.

Now this sdk supports following distributed transaction features:
1. TCC
2. Saga
3. two-phase commit

## Quick Start
1. pom file
```xml
<dependency>
    <groupId>com.jsrdxzw.github</groupId>
    <artifactId>dtm-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```
2. dtm server configuration
```yaml
dtm:
  http-server: http://localhost:36789/api/dtmsvr # dtm server address

# your project configuration
spring:
   datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      username:
      password:
      url: jdbc:mysql://localhost:3306/{project}
```
3. execute sql files in [dtm](https://github.com/dtm-labs/dtm)
   1. dtmcli.barrier.mysql.sql.
   2. If you want to use barrier，create `barrier table` using dtmsvr.storage.mysql.sql.
4. use `@EnableDtm` to start auto-configuration in spring boot.
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

AP Usage
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

DM Usage

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

AP Usage
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

DM Usage
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

### two-phase commit
```java
public void doSomething() {
     TransReq req = TransReq.builder().amount(BigDecimal.valueOf(30)).build();
     Msg msg = new Msg(httpClient).add(HOST + "/SagaBTransOut", req).add(HOST + "/SagaBTransIn", req);
     msg.prepare(HOST + "/query");
     // ... your business logic
     msg.submit();
}
```

### more examples

please see [dtm-spring-boot-starter](https://github.com/jsrdxzw/dtm-spring-boot-example) examples to learn more details.