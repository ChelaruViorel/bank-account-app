# account-requester REST backend
The account-requester SpringBoot app takes fast and safe the request to create a bank account from the user without actually processing the request. The entrypoint in the API is the RequestAccountController.

## What is the benefit of implementing the RequestAccountController asynchronously with @Async and CompletableFuture ?

The controller RequestAccountController is implemented asynchronously. What does this mean ? For example if you look at the POST method to request an account, instead of returning a RequestAccountResponse object
```
RequestAccountResponse requestAccount(@PathVariable String accountType, @RequestBody RequestAccountData requestAccountData) 
```
it returns CompletableFuture<RequestAccountResponse>
```
CompletableFuture<RequestAccountResponse> requestAccount(@PathVariable String accountType, @RequestBody RequestAccountData requestAccountData) 
```
