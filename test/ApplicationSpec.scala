import controllers.{Db, Secured, User}
import org.junit.runner._
import org.specs2.mock._
import org.specs2.runner._
import play.api.mvc.Controller
import play.api.test._


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends PlaySpecification with Mockito {

  object FakeDb {
    def apply(): Db = {
      val db: Db = mock[Db]
      db.user(anyString) returns Some(User(""))
      db
    }
  }

  object FakeSecuredController {
    def apply(database: Db) = new Controller with Secured {
      val db: Db = database
      def action = isAuthenticated { user => implicit request =>
        Ok("")
      }
    }
  }

  "Application" should {

    "block unauthenticated requests to index" in new WithApplication() {
      val controller = FakeSecuredController(FakeDb())
      val result = call(controller.action(), FakeRequest())
      status(result) must equalTo(SEE_OTHER)
    }

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(SEE_OTHER)
    }
  }
}
