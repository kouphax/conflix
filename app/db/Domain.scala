package db

import java.sql.Timestamp

import org.squeryl.adapters.H2Adapter
import org.squeryl.{KeyedEntity, PrimitiveTypeMode, Schema, Session}

object Domain extends PrimitiveTypeMode {

  // -------- SETTING UP AND INITIALISE

  import org.squeryl.SessionFactory

  Class.forName("org.h2.Driver")

  SessionFactory.concreteFactory = Some(()=>
    Session.create(
      java.sql.DriverManager.getConnection("jdbc:h2:mem:play"),
      new H2Adapter))

  // -------- DOMAIN DECLARATION

  class DbObject extends KeyedEntity[Long] {
    val id: Long = 0
    val last_update = new Timestamp(System.currentTimeMillis())
  }

  case class Video(var title: String, var tags: Array[String]) extends DbObject
  case class User(var email: String, var password: String) extends DbObject

  // -------- SCHEMA DECLARATION

  object Database extends Schema {

    val videos = table[Video]
    val users = table[User]

    on(videos)(v => declare(v.id is autoIncremented))
    on(users)(v => declare(v.id is autoIncremented))
  }
}

