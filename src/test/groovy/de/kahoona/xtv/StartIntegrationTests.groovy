package de.kahoona.xtv

import org.junit.Test
import org.junit.runner.RunWith
import org.vertx.testtools.ScriptClassRunner
import org.vertx.testtools.TestVerticleInfo

@TestVerticleInfo(filenameFilter=".+\\.groovy", funcRegex="def[\\s]+(test[^\\s(]+)")
@RunWith(ScriptClassRunner.class)
class StartIntegrationTests {
  @Test
  public void __vertxDummy() {
  }
}
