package controllers

import controllers.Fakery.{FakeDb, FakeSecuredController}
import org.junit.runner._
import org.specs2.mock._
import org.specs2.runner._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class SecuredSpec extends PlaySpecification with Mockito {

  "Secured trait" should {
    "block unauthenticated requests to authenticated actions" in  {
      val controller = FakeSecuredController(FakeDb())
      val result = call(controller.action(), FakeRequest())
      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/login")
    }

    "block non existent user on authenticated actions" in new WithApplication()  {
      val controller = FakeSecuredController(FakeDb("james"))
      val result = call(controller.action(), FakeRequest().withSession("email" -> "nope"))
      status(result) must equalTo(SEE_OTHER)
      redirectLocation(result) must beSome("/login")
    }

    "allow existing user on authenticated actions" in new WithApplication()  {
      val controller = FakeSecuredController(FakeDb("james"))
      val result = call(controller.action(), FakeRequest().withSession("email" -> "james"))
      status(result) must equalTo(OK)
      contentAsString(result) must equalTo("securedAction")
    }

  }
}
