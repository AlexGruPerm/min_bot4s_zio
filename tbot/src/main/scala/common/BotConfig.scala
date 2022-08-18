package common

import com.typesafe.config.Config

case class BotConfig(
                      token: String,
                      webhookUrl: String,
                      webhook_port: Int,
                      keyStorePassword: String,
                      pubcertpath: String,
                      p12certpath: String,
                    )

case object BotConfigHelper {
  def getConfig(fileConfig :Config): BotConfig = {
    val telegramPrefix = "telegram."
    BotConfig(
      token = fileConfig.getString(telegramPrefix+"token"),
      webhookUrl = fileConfig.getString(telegramPrefix+"webhookUrl"),
      webhook_port = fileConfig.getInt(telegramPrefix+"webhook_port"),
      keyStorePassword = fileConfig.getString(telegramPrefix+"keyStorePassword"),
      pubcertpath = fileConfig.getString(telegramPrefix+"pubcertpath"),
      p12certpath = fileConfig.getString(telegramPrefix+"p12certpath")
    )
  }
}

