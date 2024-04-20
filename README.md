Wallet Reproducer
=================

Reproduces a `NullPointerException` caused by `oracle.security.pki.OracleWallet#n` in `oracle.security.pki.OracleWallet#open(String, char[])`.

Only happens on Java 21, does not happen on Java 17.

Issue
-----

The underlying issue is that `javax.sql.DataSource#getConnection()` of ojdbc11 is not thread safe on Java 21 when using [wallets](https://docs.oracle.com/cd/F95115_01/pt861pbr2/eng/pt/tsvt/UnderstandingOracleWallet.html).

Running
-------

```
export JAVA_HOME=
export WALLET_LOCATION=
sh run.sh
```