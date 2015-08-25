package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.github.t3hnar.bcrypt._
import play.api.i18n.Messages.Implicits._

case class LoginForm(username: String, password: String)

object LoginForm {
  val mapper = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )
}

class Authentication @Inject()(val db: Db, val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport {

  def showLogin = Action { implicit request =>
    Ok(views.html.login(LoginForm.mapper))
  }

  def doLogin = Action { implicit request =>
    val badResponse = Redirect(routes.Authentication.showLogin)
      .flashing("message" -> "Invalid username or password")
    LoginForm.mapper.bindFromRequest().fold(
      invalidForm => badResponse,
      validForm => {
        db.user(validForm._1).fold(badResponse) {
          case User(username, password) if validForm._2.isBcrypted(password) =>
            Redirect(routes.Application.index).withSession("email" -> username)
        }
      })
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withNewSession
  }
}

class Application @Inject()(val db: Db) extends Controller with Secured {

  def index = isAuthenticated { user => implicit request =>
    Ok(views.html.index(user.name))
  }

  def login = Action {
    Ok("You need to log in")
  }

  def setSession = Action {
    Redirect(routes.Application.index).withSession("email" -> "james@james.com")
  }

  def unsetSession = Action {
    Redirect(routes.Application.index).withNewSession
  }

}

case class User(name: String, password: String)

@Singleton
class Db {

  val Users = Map(
    "james" -> User("james", "password".bcrypt)
  )

  def user(name: String): Option[User] = Users.get(name)
}


trait Secured {

  val db: Db

  def user(request: RequestHeader): Option[User] = {
    request.session.get("email").flatMap(db.user)
  }

  def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Authentication.showLogin).withNewSession
  }

  def isAuthenticated(f: => User => Request[AnyContent] => Result) = {
    Security.Authenticated(user, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }
}
