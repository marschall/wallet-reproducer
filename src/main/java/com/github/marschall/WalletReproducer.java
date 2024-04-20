package com.github.marschall;

import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

public final class WalletReproducer {

  private final DataSource dataSource;
  private final int threadCount;

  WalletReproducer(DataSource dataSource, int threadCount) {
    this.dataSource = dataSource;
    this.threadCount = threadCount;
  }

  void reproduce() {
    AtomicBoolean stop = new AtomicBoolean(false);
    CountDownLatch start = new CountDownLatch(this.threadCount);
    StopAllThreads stopAllThreads = new StopAllThreads(stop);
    for (int i = 0; i < this.threadCount; i++) {
      Thread thread = new Thread(new GetConnectionHammer(start, stop, dataSource), "hammer-" + i);
      thread.setUncaughtExceptionHandler(stopAllThreads);
      thread.start();
    }
  }

  static final class StopAllThreads implements UncaughtExceptionHandler {

    private final AtomicBoolean stop;

    StopAllThreads(AtomicBoolean stop) {
      this.stop = stop;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
      this.stop.set(true);
      e.printStackTrace(System.err);
    }

  }

  static final class GetConnectionHammer implements Runnable {

    private final CountDownLatch start;
    private final AtomicBoolean stop;
    private final DataSource dataSource;

    GetConnectionHammer(CountDownLatch start, AtomicBoolean stop, DataSource dataSource) {
      this.start = start;
      this.stop = stop;
      this.dataSource = dataSource;
    }

    @Override
    public void run() {
      try {
        this.start.countDown();
        this.start.await();
        while (!this.stop.get()) {
          try (Connection connection = this.dataSource.getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1 FROM dual");
              ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
              resultSet.getInt(1);
            }
          }
        }
      } catch (InterruptedException | SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void main(String[] args) throws SQLException {
    OracleDataSource dataSource = new OracleDataSource();
    dataSource.setURL(System.getenv("APP_DB_URL"));

    Properties properties = new Properties();
    properties.put(OracleConnection.CONNECTION_PROPERTY_THIN_VSESSION_PROGRAM, WalletReproducer.class.getSimpleName());
    properties.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "100");
    properties.put(OracleConnection.CONNECTION_PROPERTY_IMPLICIT_STATEMENT_CACHE_SIZE, "50");
    properties.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_CONNECTION_VALIDATION, OracleConnection.ConnectionValidation.SOCKET.name());
    dataSource.setConnectionProperties(properties);

    int threadCount = 10;
    new WalletReproducer(dataSource, threadCount).reproduce();
  }

}
