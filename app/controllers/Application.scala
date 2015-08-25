package controllers

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import com.github.t3hnar.bcrypt._
import play.api.i18n.Messages.Implicits._
import db.Domain._
import db.Domain.Database._

case class LoginForm(username: String, password: String)

class Authentication @Inject()(val messagesApi: MessagesApi) extends Controller with Secured with I18nSupport {

  val mapper = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  def showLogin = Action { implicit request =>
    Ok(views.html.login(mapper))
  }

  def doLogin = Action { implicit request =>
    val badResponse = Redirect(routes.Authentication.showLogin)
      .flashing("message" -> "Invalid username or password")
    mapper.bindFromRequest().fold(
      invalidForm => badResponse,
      validForm => transaction {
        from(users)(u => where(u.email === validForm._1) select(u)).singleOption.fold(badResponse) {
          case User(email, password) if validForm._2.isBcrypted(password) =>
            Redirect(routes.Application.index).withSession("email" -> email)
        }
      })
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index).withNewSession
  }
}

class Application @Inject()() extends Controller with Secured {

  def index = isAuthenticated { user => implicit request =>
    Ok(views.html.index(user.email))
  }

  def login = Action {
    Ok("You need to log in")
  }

  def setSession = Action {
    Redirect(routes.Application.index).withSession("email" -> "james")
  }

  def unsetSession = Action {
    Redirect(routes.Application.index).withNewSession
  }

}

trait Secured {

  def user(request: RequestHeader): Option[User] = {
    request.session.get("email").flatMap { e =>
      transaction {
        from(users)(u => where(u.email === e) select u)
          .singleOption
      }
    }
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
