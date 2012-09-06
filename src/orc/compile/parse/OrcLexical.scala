//
// OrcLexical.scala -- Scala class OrcLexical
// Project OrcScala
//
// $Id: OrcLexical.scala 2985 2012-03-17 19:33:26Z laurenyew $
//
// Created by jthywiss on Jun 1, 2010.
//
// Copyright (c) 2011 The University of Texas at Austin. All rights reserved.
//
// Use and redistribution of this file is governed by the license terms in
// the LICENSE file found in the project's top-level directory and also found at
// URL: http://orc.csres.utexas.edu/license.shtml .
//

package orc.compile.parse

import java.text.Normalizer
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.CharSequenceReader.EofCh
import scala.collection.mutable.HashSet

import java.util.regex.Pattern

/** Lexical scanner (tokenizer) for Orc.  This extends and overrides
  * <code>scala.util.parsing.combinator.lexical.StdLexical</code>
  * with Orc's lexical definitions.
  *
  * @author jthywiss
  */
class OrcLexical() extends StdLexical() with RegexParsers {
  override type Elem = Char

  ////////
  // Character categories
  ////////

  // Identifiers per Unicode Standard Annex #31, Unicode Identifier and Pattern Syntax
  val identifier: Parser[String] = """[\p{javaUnicodeIdentifierStart}][\p{javaUnicodeIdentifierPart}']*""".r
  override protected def processIdent(name: String) = super.processIdent(Normalizer.normalize(name, Normalizer.Form.NFC))

  // StdLexical wants a func for legal identifier chars other than digits
  override def identChar = elem("identifier character", { ch => ch.isUnicodeIdentifierPart || ch == '\'' })

  // Per Unicode standard section 5.8, Newline Guidelines, recommendation R4
  // "A readline function should stop at [CR, LF, CRLF, NEL[0085]], LS[2028], FF, or PS[2029]"
  val unicodeNewlineChars = "\r\n\u0085\u2028\f\u2029"

  // Per Unicode Standard Annex #31, section 4, recommendation R3
  // "...programming language can define its own whitespace characters ... relative to the Unicode Pattern_White_Space ... characters"
  val unicodeWhitespaceChars = "\t\u000B \u200E\u200F" + unicodeNewlineChars

  override def whitespaceChar = elem("space char", unicodeWhitespaceChars.contains(_)) //From Lexical
  override protected val whiteSpace = ("[" + Pattern.quote(unicodeWhitespaceChars) + "]+").r //From RegexParsers

  override def skipWhitespace = false
  override protected def handleWhiteSpace(source: java.lang.CharSequence, offset: Int): Int = offset

  ////////
  // Lists used by the token parsers, along with supporting functions
  ////////

  /** The set of reserved identifiers: these will be returned as `Keyword's   
   WHAT THESE ARE: "reserved" Keywords are used for declarations (Ex: def) when we are creating variables,SecurityLevels, etc, for later use  
   */ 
  // All these string literals are assumed to be in Unicode Normalization Form C (NFC)
  // Note: DeclSL is short for DeclareSecurityLevel. This is for when we create/edit security levels. (this can be done locally too)
  // Ex: DeclSL A (B) () -- Declares a new/edits SL A with parents B (top level is implicit), and no children (bottom level is implicit)
  override val reserved = new HashSet[String] ++ List(
    "as", "def", "else", "if", "import", "include",
    "lambda", "signal", "stop", "then", "type", "val",
    "true", "false", "null", "DeclSL", "_")

  val operators = List(
    "+", "-", "*", "/", "%", "**",
    "&&", "||", "~",
    "<", ">",
    "=", "<:", ":>", "<=", ">=", "/=",
    ":",
    ".", "?", ":=")

  /** The set of delimiters (ordering does not matter) 
   * WHAT THESE ARE: Delimiters are used for attaching patterns (ex: variables) or expressions 
   * to Types, SecurityLevels, etc
   */
   // Note: Use of "@":
   //    Summary: this is for when we want to attach an already created SL to a variable or expression
   //     Ex: (var x:: Int @A)-- This attaches SL A to var x which is an integer
  override val delimiters /* and operators */ = new HashSet[String] ++ (List(
    "(", ")", "[", "]", "{.", ".}", ",",
    "|", ";",
    "::", ":!:","@" ) ::: operators)
    

  protected lazy val operRegex = {
    val o = new Array[String](operators.size)
    operators.copyToArray(o, 0)
    scala.util.Sorting.quickSort(o)(new Ordering[String] { def compare(x: String, y: String) = y.length - x.length })
    o.toList.map(Pattern.quote(_)).mkString("|").r
  }

  protected lazy val delimOperRegex = {
    val o = new Array[String](delimiters.size)
    delimiters.copyToArray(o, 0)
    scala.util.Sorting.quickSort(o)(new Ordering[String] { def compare(x: String, y: String) = y.length - x.length })
    o.toList.map(Pattern.quote(_)).mkString("|").r
  }

  ////////
  // Token types in addition to those in StdTokens and Tokens
  ////////

  case class FloatingPointLit(chars: String) extends Token {
    override def toString = chars
  }

  def numberToken(s: String) = {
    if (s.contains('.') || s.contains('e') || s.contains('E')) FloatingPointLit(s) else NumericLit(s)
  }

  ////////
  // Token parsers, in bottom-up order
  ////////

  def multiLineCommentBody: Parser[Any] =
    """(?s).*?(?=((\{-)|(-\})))""".r ~
      ("-}"
        | "{-" ~ multiLineCommentBody ~ multiLineCommentBody)

  override val whitespace: Parser[Any] =
    rep(("[" + Pattern.quote(unicodeWhitespaceChars) + "]+").r
      | ("--[^" + Pattern.quote(unicodeNewlineChars) + "]*").r
      | "{-" ~ multiLineCommentBody
      | '{' ~ '-' ~ err("unclosed comment"))

  val numberLit: Parser[String] =
    """([0-9]+)([.][0-9]+)?([Ee][+-]?([0-9]+))?""".r

  val stringLit: Parser[String] =
    '\"' ~> (('\\' ~> chrExcept(EofCh) ^^ { case 'f' => "\f"; case 'n' => "\n"; case 'r' => "\r"; case 't' => "\t"; case c => c.toString }
      | ("[^\\\\\"" + Pattern.quote(unicodeNewlineChars) + "]+").r)*) <~ '\"' ^^ { _.mkString }

    //special cases for delimiters
  override val token: Parser[Token] = (whitespace?) ~>
    (identifier ^^ { processIdent(_) }
      | '_' ^^^ Keyword("_")
      | '(' ~> operRegex <~ ')' ^^ { Identifier(_) }
      | "(0-)" ^^^ Identifier("0-")
      | numberLit ^^ { numberToken(_) }
      | stringLit ^^ { StringLit(_) }
      | '\"' ~> err("unclosed string literal")
      | // Must be after other alternatives that a delim could be a prefix of
      delimOperRegex ^^ { Keyword(_) }
      | EofCh ^^^ EOF)

}
