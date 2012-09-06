//
// OrcJSON.scala -- Scala class ReadJSON and objects OrcJSONLexical and OrcJSONParser
// Project OrcScala
//
// $Id: ReadJSON.scala 2933 2011-12-15 16:26:02Z jthywissen $
//
// Created by dkitchin on Oct 25, 2010.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//
package orc.lib.web

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator.lexical._
import scala.util.parsing.input.StreamReader.EofCh
import scala.util.parsing.input.CharSequenceReader

import orc.values.sites.{ TotalSite, UntypedSite }
import orc.values.OrcRecord
import orc.error.runtime.{ ArgumentTypeMismatchException, ArityMismatchException, SiteException }

/** JSON reader, converting a JSON string to an Orc value.
  *
  * @author dkitchin
  */
class ReadJSON extends TotalSite with UntypedSite {

  def evaluate(args: List[AnyRef]): AnyRef = {
    args match {
      case List(s: String) => OrcJSONParser.parse(s)
      case List(z) => throw new ArgumentTypeMismatchException(0, "String", z.getClass().toString())
      case _ => throw new ArityMismatchException(1, args.size)
    }
  }

}

/** JSON lexical scanner.  Returns stream of StringLit, NumericLit, Keyword, and EOF.
  *
  * See ECMA-262 section 15.12.1, The JSON Grammar, and RFC 4627.
  *
  * (Portions derived from Scala's scala.util.parsing.json.Lexer.)
  *
  * @author dkitchin
  * @author jthywiss
  */
object OrcJSONLexical extends StdLexical with RegexParsers {

  override type Elem = Char

  override def whitespaceChar = elem("whitespace char", ch => (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t'))

  override def whitespace = """[ \n\r\t]*""".r

  override def comment = null //Illegal in JSON

  override def identChar = letter | elem('_') | elem('$')

  reserved ++= List("true", "false", "null")

  delimiters ++= List("{", "}", "[", "]", ":", ",")

  override def processIdent(name: String) =
    if (reserved contains name) Keyword(name) else ErrorToken("Unrecognized JSON keyword `" + name + "'")

  def hexDigit = """[0123456789abcdefABCDEF]""".r

  private def unicodeBlock = hexDigit ~ hexDigit ~ hexDigit ~ hexDigit ^^ {
    case a ~ b ~ c ~ d =>
      new String(Array(Integer.parseInt(List(a, b, c, d) mkString "", 16)), 0, 1)
  }

  def escapeSeq: Parser[String] = (
    '\\' ~ '\"' ^^^ "\""
    | '\\' ~ '\\' ^^^ "\\"
    | '\\' ~ '/' ^^^ "/"
    | '\\' ~ 'b' ^^^ "\b"
    | '\\' ~ 'f' ^^^ "\f"
    | '\\' ~ 'n' ^^^ "\n"
    | '\\' ~ 'r' ^^^ "\r"
    | '\\' ~ 't' ^^^ "\t"
    | '\\' ~> 'u' ~> unicodeBlock)

  def stringChars = rep(escapeSeq | """[^\u0000-\u001f\\\"]+""".r)

  override def token: Parser[Token] = (
    '\"' ~ stringChars ~ '\"' ^^ { case '\"' ~ chars ~ '\"' => StringLit(chars mkString "") }
    | identChar ~ rep(identChar | digit) ^^ { case first ~ rest => processIdent(first :: rest mkString "") }
    | """[-]?(([1-9][0-9]*)|0)([.][0-9]+)?([Ee][+-]?([0-9]+))?""".r ^^ { NumericLit(_) }
    | EofCh ^^^ EOF
    | '\"' ~> failure("unclosed string literal")
    | delim
    | failure("illegal character"))

}

/** Modified version of Scala's JSON parser, returning Orc values instead.
  *
  * See ECMA-262 section 15.12.1, The JSON Grammar and RFC 4627.
  *
  * @author dkitchin
  * @author Derek Chen-Becker <"java"+@+"chen-becker"+"."+"org">
  */
object OrcJSONParser extends StdTokenParsers {

  type Tokens = OrcJSONLexical.type
  override val lexical = OrcJSONLexical

  // Define the grammar
  def jsonText = value
  def jsonObj = ("{" ~> repsep(objEntry, ",") <~ "}") ^^ { new OrcRecord(_) }
  def jsonArray = "[" ~> repsep(value, ",") <~ "]"
  def objEntry = stringLit ~ (":" ~> value) ^^ { case x ~ y => (x, y) }
  def value: Parser[AnyRef] = (
    jsonObj
    | jsonArray
    | "true" ^^^ java.lang.Boolean.TRUE
    | "false" ^^^ java.lang.Boolean.FALSE
    | "null" ^^^ null
    | numericLit ^^ { BigDecimal(_) }
    | stringLit)

  def parse(json: String): AnyRef = {
    phrase(jsonText)(new lexical.Scanner(new CharSequenceReader(json))) match {
      case Success(v, _) => v
      case n: NoSuccess => {
        throw new SiteException("JSON parsing failed: " + n.msg + "\n" + n.next.pos.longString)
        // None
      }
    }
  }

}
