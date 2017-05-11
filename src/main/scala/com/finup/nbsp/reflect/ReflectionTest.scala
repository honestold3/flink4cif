package com.finup.nbsp.reflect

import scala.reflect.runtime.{universe => ru}

/**
  * Created by wq on 2017/5/11.
  */

trait theTrait { def theMethod(x: String): Unit }

// the different logic held in different objects
object object1 extends theTrait {
  def theMethod(x: String) = { println("a " + x ) }
}

object object2 extends theTrait {
  def theMethod(x: String) = { println("b " + x ) }
}

object object3 extends theTrait {
  def theMethod(x: String) = { println("c " + x ) }
}

// run static/dynamic reflection methods
object ReflectionTest extends App{

  // "static" invocation calling object1.theMethod
  def staticInvocation() = {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val im = m.reflect(com.finup.nbsp.reflect.object1)
    val method = ru.typeOf[com.finup.nbsp.reflect.object1.type].decl(ru.TermName("theMethod")).asMethod
    val methodRun = im.reflectMethod(method)
    methodRun("test")
  }

  staticInvocation

  // "dynamic" invocation using integer to call different methods
  def dynamicInvocation( y: Integer) = {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val module = m.staticModule("com.finup.nbsp.reflect.object" + y)
    val im = m.reflectModule(module)
    val method = im.symbol.info.decl(ru.TermName("theMethod")).asMethod

    val objMirror = m.reflect(im.instance)
    objMirror.reflectMethod(method)("test1")

  }

  dynamicInvocation(1)
  dynamicInvocation(2)
  dynamicInvocation(3)

}
