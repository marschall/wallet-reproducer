Wallet Reproducer
=================

Reproduces a `NullPointerException` caused by ``oracle.security.pki.OracleWallet#n`` in `oracle.security.pki.OracleWallet#open(String, char[])`.

Only happens on Java 21, does not happen on Java 17.


Running
-------

```
export JAVA_HOME=
export WALLET_LOCATION=
sh run.sh
```