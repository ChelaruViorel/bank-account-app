# bank-account-app
Demo application exercise for creating a bank account for existing clients.
A user which is the bank's client, should be able to access a page to open a new savings bank account. The opening of the account can happen only Monday-Friday and between hours 9-18.

We assume that the user is already logged, so we are not interested in the authentication.

We concentrate our exercise only on the opening of the account. We make a clear distinction between acknowledging the request of the client and the actual opening of the account. Because we, internally, have the restriction that we cannot open an account anytime but only in specific days and hours, this doesn't mean that we respond to the client that we cannot honour his request for exampple on a Sunday at 13:00. We will take his request any time 24/7, and we inform him that his request to create an account was acknowledged and he will be informed when the account is created. Meanwhile we will create the account in the background according to the internal restrictions.
