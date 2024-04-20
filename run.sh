${JAVA_HOME}/bin/java -cp lib/oraclepki-21.13.0.0.jar:lib/osdt_core-21.13.0.0.jar:lib/ojdbc11-21.13.0.0.jar:lib/osdt_cert-21.13.0.0.jar:lib/wallet-reproducer-1.0.0-SNAPSHOT.jar \
  -Xms2g -Xmx2g \
  -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m \
  -Djava.net.preferIPv4Stack=true \
  -Djava.awt.headless=true \
  -Xss512k \
  -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:-EnableDynamicAgentLoading \
  -XX:+ParallelRefProcEnabled \
  -XX:ParallelGCThreads=8 \
  -XX:ConcGCThreads=6 \
  -Duser.timezone=Europe/Zurich \
  -XX:+DisableExplicitGC \
  -Doracle.net.wallet_location=${WALLET_LOCATION} \
  com.github.marschall.WalletReproducer