package controllers

import org.specs2.mock.Mockito
import play.api.mvc.Controller

object Fakery extends Mockito {

  object FakeDb {
    def apply(usernames: String*): Db = {
      val db: Db = mock[Db]

      db.user(anyString) returns None
      for(username <- usernames) {
        db.user(username) returns Some(User(username))
      }

      db
    }
  }

  object FakeSecuredController {
    def apply(database: Db) = new Controller with Secured {
      val db: Db = database
      def action = isAuthenticated { user => implicit request =>
        Ok("securedAction")
      }
    }
  }
}
