package code.api.berlin.group.v1

import code.api.ResourceDocs1_4_0.SwaggerDefinitionsJSON.coreAccountsJsonV300
import code.api.util.APIUtil._
import code.api.util.ApiVersion
import code.api.util.ErrorMessages.{UnknownError, UserNotLoggedIn}
import code.bankconnectors.Connector
import code.views.Views
import net.liftweb.http.rest.RestHelper

import scala.collection.immutable.Nil
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global

trait APIMethods1 {
  //needs to be a RestHelper to get access to JsonGet, JsonPost, etc.
  self: RestHelper =>

  val Implementations1 = new Object() {
    val implementedInApiVersion: ApiVersion = ApiVersion.v1 // was noV

    val resourceDocs = ArrayBuffer[ResourceDoc]()
    val apiRelations = ArrayBuffer[ApiRelation]()
    val codeContext = CodeContext(resourceDocs, apiRelations)


    resourceDocs += ResourceDoc(
      corePrivateAccountsAllBanks1,
      implementedInApiVersion,
      "corePrivateAccountsAllBanks1",
      "GET",
      "/accounts",
      "Experimental - BG Get Accounts at all Banks",
      s"""Get private accounts at all banks.
         |Returns the list of accounts containing private views for the user at all banks.
         |Please ignore example JSON for now.
         |
        |${authenticationRequiredMessage(true)}
         |""",
      emptyObjectJson,
      coreAccountsJsonV300,
      List(UserNotLoggedIn,UnknownError),
      Catalogs(Core, PSD2, OBWG),
      List(apiTagBerlinGroup, apiTagAccount, apiTagPrivateData))


    apiRelations += ApiRelation(corePrivateAccountsAllBanks1, corePrivateAccountsAllBanks1, "self")



    lazy val corePrivateAccountsAllBanks1 : OBPEndpoint = {
      //get private accounts for all banks
      case "accounts" :: Nil JsonGet _ => {
        cc =>
          for {
            (user, callContext) <- extractCallContext(UserNotLoggedIn, cc)
            u <- unboxFullAndWrapIntoFuture{ user }
            availablePrivateAccounts <- Views.views.vend.getPrivateBankAccountsFuture(u)
            coreAccounts <- {Connector.connector.vend.getCoreBankAccountsFuture(availablePrivateAccounts, callContext)}
          } yield {
            (JSONFactory_v1.createCoreAccountsByCoreAccountsJSON(coreAccounts.getOrElse(Nil)), callContext)
          }
      }
    }



  }

}


object APIMethods1 {
}
