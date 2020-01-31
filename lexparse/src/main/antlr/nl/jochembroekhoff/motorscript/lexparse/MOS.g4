/*
The ANTLR4 grammar definition for MotorScript v2.
*/

grammar MOS;

/***********************************************************************************************************************
 ** Parser rules                                                                                                      **
 **********************************************************************************************************************/

/**
 ** Section: entry
 **/

script
    : useDeclarations? topLevelItem* EOF
    ;

/**
 ** Section: identifier, path and soft keywords
 **/

softKeyword
    : KwAlias
    | KwAlign
    | KwAnchored
    | KwAs
    | KwAt
    | KwDefault
    | KwEnum
    | KwEyes
    | KwFacing
    | KwFeet
    | KwFor
    | KwIn
    | KwIterator
    | KwPositioned
    | KwRotated
    | KwUse
    | KwUser
    ;

path
    : ( pathNamespace? Colon )? pathName
    ;

pathNamespace
    : identifier
    ;

pathName
    : identifier ( SlashBw identifier )*
    ;

identifier
    : Identifier
    | softKeyword
    ;

/*
 * Code structure
 */

topLevelItem
    : functionDeclaration
    | aliasDeclaration
    | typeSpecification
    | declarationStatement
    ;

block
    : statement ColonSemi?
    | AccL ( statement ColonSemi? )* AccR
    ;

statement
    : declarationStatement
    | deferStatement
    | executeStatement
    | forStatement
    | ifStatement
    | switchStatement
    | returnStatement
    | yieldStatement
    | expressionStatement
    ;

/**
 ** Section: expression basics
 **/

/*
 * Expression
 */

// TODO: Validate precedence
// TODO: Make less complex (maybe), this part takes a lot of parse time because it is very ambiguous
expression
    : ParenL expression ParenR
    | expression find+
    | expression invocation
    | expression postfix
    |<assoc=right> prefix expression
   // infix ops
    | expression ( arithmeticMulDivMod expression )+
    | expression ( arithmeticPlusMinus expression )+
    | expression ( AmpDouble expression )+
    | expression ( PipeDouble expression )+
   // compare
    | expression compare expression
   // ranges
    | expression DotDot expression?
    | rangeAndLower
   // assignment
    |<assoc=right> expression assign expression
   // misc
    | literal
    | selector
    | resource
    | tag
    | nbt
    | position
    | vector
    | identifier
    | path
    | typeEnumReference
    ;

prefix
    : Exclam
    | PlusDouble
    | MinusDouble
    ;

postfix
    : PlusDouble
    | MinusDouble
    ;

arithmeticMulDivMod
    : Asterisk
    | SlashFw
    | Percent
    ;

arithmeticPlusMinus
    : Plus
    | Minus
    ;

/*
 * Properties
 */

properties
    : BrackL ( property ( Comma property )* Comma? )? BrackR
    ;

property
    : ( Exclam? KwIs )? expression
    | identifier Equals expression
    ;

/*
 * Find
 */

find
    : findIndex
    | findPath
    ;

findPath
    : Dot identifier
    | Dot literalString
    ;

findIndex
    : BrackL expression BrackR
    ;

/*
 * Type
 */

type
    : path
    ;

typeReference
    : LessThan type GreaterThan
    ;

typeEnumReference
    : LessThan type type GreaterThan
    ;

/**
 ** Section: expressions detailed
 **/

/*
 * Literal
 */

literal
    : literalString
    | literalBoolean
    | literalNumeric
    ;

literalNumeric
    : literalReal
    | literalInteger
    ;

literalString : String ;
literalBoolean : KwTrue | KwFalse ;
literalReal : Real ;
literalInteger : Integer ;

/*
 * Selector
 */

selector
    : At identifier properties?
    ;

/*
 * Resource and tag
 */

resource
    : Dollar path properties?
    ;

tags
    : tag+
    ;

tag
    : Hash path
    ;

/*
 * Range
 */

// Note: other parts of ranges are defined in the "expression" rule

rangeAndLower
    : DotDot expression
    ;

/*
 * NBT
 */

nbt
    : nbtCompound
    | nbtArray
    | nbtList
    ;

nbtCompound
    : AccL ( nbtCompoundKeyValue ( Comma nbtCompoundKeyValue )* Comma? )? AccR
    ;

nbtCompoundKeyValue
    : nbtCompoundKey Colon expression
    ;

nbtCompoundKey
    : String
    | identifier
    ;

nbtArray
    : BrackL nbtArrayType ColonSemi expressionList? BrackR
    ;

nbtArrayType
    : type
    ;

nbtList
    : BrackL expressionList? BrackR
    ;

/*
 * Position
 */

position
    : Backtick ( positionTilde | positionCaret ) Backtick
    ;

positionTilde
    : positionTildePart positionTildePart positionTildePart
    ;

positionTildePart
    : Tilde expression?
    | expression
    ;

positionCaret
    : positionCaretPart positionCaretPart positionCaretPart
    ;

positionCaretPart
    : Caret expression?
    ;

/*
 * Vector (tuple)
 */

vector
    : LessThanDouble expression* GreaterThanDouble
    ;

/**
 ** Section: functions & invocation
 **/

signature
    : ParenL parameters? ParenR
    ;

parameters
    : parameterGroup ( Comma parameterGroup )* Comma?
    ;

parameterGroup
    : identifier parameterType
    | ( identifier Comma )+ identifier parameterType
    ;

parameterType
    : DotDotDot? type
    ;

arguments
    : expressionList? expressionListNamedTail?
    ;

expressionList
    : expression ( Comma expression )* Comma?
    ;

expressionListNamedTail
    : ( Comma identifier Equals expression )+
    ;

invocation
    : ParenL arguments ParenR
    ;

functionDeclaration
    // TODO: Annotations
    : tags? functionModifiers? KwFunc identifier signature? ( ArrR type )? functionBody
    ;

functionModifiers
    : functionModifier+
    ;

functionModifier
    : KwDefault
    | KwIterator
    | KwPrivate
    | KwPublic
    | KwUser
    ;

functionBody
    : ArrFatR expressionStatement
    | block
    ;

/**
 ** Section: statements
 **/

/*
 * Declaration statement (for var, val and const)
 */

declarationStatement
    : declarationModifiers? ( KwVar | KwVal | KwConst ) declarationTarget typeReference? assign expression
    ;

declarationTarget
    : identifier
    | vector
    ;

declarationModifiers
    : declarationModifier+
    ;

declarationModifier
    : KwPrivate
    | KwPublic
    ;

/*
 * Defer statement
 */

deferStatement
    : KwDefer expression
    ;

/*
 * Execute statement
 */

executeStatement
    : executeChainPart+ block
    ;

/*
TODO:
    executeRotated
*/
executeChainPart
    : executeAlign
    | executeAnchored
    | executeAs
    | executeAt
    | executeFacing
    | executeIn
    | executePositioned
    | executeRotated
    ;

executeAlign
    : KwAlign literalString
    ;

executeAnchored
    : KwAnchored ( KwFeet | KwEyes )
    ;

executeAs
    : KwAs expression
    ;

executeAt
    : KwAt expression
    ;

executeFacing
    : KwFacing expression
    ;

executeIn
    : KwIn expression
    ;

executePositioned
    : KwPositioned KwAs? expression
    ;

executeRotated
    : KwRotated KwAs expression
    | KwRotated expression
    ;

/*
 * For statement
 */

forStatement
    : forInfinite
    | forIn
    | forWhile
    ;

forInfinite
    : KwFor block
    ;

// TODO: Change expressionList to something like identifierList
forIn
    : KwFor expressionList ArrL expression block
    ;

forWhile
    : KwFor expression block
    ;

/*
 * If statement
 */

ifStatement
    : ifMainBranch ifElseIfBranch* ifElseBranch?
    ;

ifMainBranch
    : KwIf expression block
    ;

ifElseIfBranch
    : KwElse KwIf expression block
    ;

ifElseBranch
    : KwElse block
    ;

/*
 * Switch statement (switch by cases, or switch by conditions)
 */

switchStatement
    : switchCases
    | switchConditions
    ;

switchCases
    : KwSwitch expression AccL switchCase+ switchDefault? AccR
    ;

switchCase
    : type block
    ;

switchConditions
    : KwSwitch AccL switchCondition+ switchDefault? AccR
    ;

switchCondition
    : expression
    ;

switchDefault
    : 'default' block
    ;

/*
 * Return statement
 */

returnStatement
    : KwReturn expression?
    ;

/*
 * Yield statement
 */

yieldStatement
    : KwYield expression
    ;

/*
 * Expression statement
 */

expressionStatement
    : expression
    ;

// TODO: ===============================================================================================================

aliasDeclaration
    : KwAlias selector selector
    ;

typeSpecification
    : typeEnumSpecification
    ;

typeEnumSpecification
    : KwEnum type AccL ( type ( Comma type )* Comma? ) AccR
    ;

useDeclarations
    : useDeclaration+
    ;

useDeclaration
    : KwUse path
    ;

compare
    : KwIs
    | KwMatches
    | LessThan
    | LessThanOrEqualTo
    | GreaterThan
    | GreaterThanOrEqualTo
    | EqualsDouble
    ;

assign
    : Equals
    | PlusEquals
    | MinusEquals
    | AsteriskEquals
    | SlashFwEquals
    | PercentEquals
    ;

/**********************************************************************************************************************
 ** Lexer rules                                                                                                      **
 **********************************************************************************************************************/

/*
 * Fragments
 */

fragment
LetterUppercase
    : [A-Z]
    ;

fragment
LetterLowercase
    : [a-z]
    ;

fragment
Letter
    : LetterUppercase
    | LetterLowercase
    ;

fragment
Number09
    : [0-9]
    ;

fragment
Number19
    : [1-9]
    ;

fragment
Number0
    : '0'
    ;

fragment
NumericNonZeroStart
    : Number19 Number09*
    ;

fragment
NumericZeroOrNonZeroStart
    : Number0
    | NumericNonZeroStart
    ;

/*
 * Literals
 */

// String literal

fragment
StringEscapeSequence
    : '\\' [tn\\]
    ;

String
    : '"' ( ~[\\"] | '\\"' | StringEscapeSequence )* '"'
    | '\'' ( ~[\\'] | '\\\'' | StringEscapeSequence )* '\''
    ;

// Numeric literals


Real
    : Number0 RealSuffix
    | NumericZeroOrNonZeroStart Dot Number09+ RealSuffix?
    ;

fragment
RealSuffix : 'd' | 'D' | 'f' | 'F' ;

Integer
    : Minus? NumericZeroOrNonZeroStart IntegerSuffix?
    ;

fragment
IntegerSuffix : 'l' | 'L' | 's' | 'S' | 'b' | 'B' ;

/*
 * Punctuation
 */

AccL                    : '{' ;
AccR                    : '}' ;
AmpDouble               : '&&' ;
ArrFatR                 : '=>' ;
ArrL                    : '<-' ;
ArrR                    : '->' ;
Asterisk                : '*' ;
AsteriskEquals          : '*=' ;
At                      : '@' ;
Backtick                : '`' ;
BrackL                  : '[' ;
BrackR                  : ']' ;
Caret                   : '^' ;
Colon                   : ':' ;
ColonSemi               : ';' ;
Comma                   : ',' ;
Dollar                  : '$' ;
Dot                     : '.' ;
DotDot                  : '..' ;
DotDotDot               : '...' ;
Equals                  : '=' ;
EqualsDouble            : '==' ;
Exclam                  : '!' ;
GreaterThan             : '>' ;
GreaterThanDouble       : '>>' ;
GreaterThanOrEqualTo    : '>=' ;
Hash                    : '#' ;
LessThan                : '<' ;
LessThanDouble          : '<<' ;
LessThanOrEqualTo       : '<=' ;
Minus                   : '-' ;
MinusDouble             : '--' ;
MinusEquals             : '-=' ;
Percent                 : '%' ;
PercentEquals           : '%=' ;
PipeDouble              : '||' ;
ParenL                  : '(' ;
ParenR                  : ')' ;
Plus                    : '+' ;
PlusDouble              : '++' ;
PlusEquals              : '+=' ;
SlashBw                 : '\\' ;
SlashFw                 : '/' ;
SlashFwEquals           : '/=' ;
Tilde                   : '~' ;

/*
 * Keywords
 */

// Allowed to be used as identifiers
KwAlias                 : 'alias' ;
KwAlign                 : 'align' ;
KwAnchored              : 'anchored' ;
KwAs                    : 'as' ;
KwAt                    : 'at' ;
KwDefault               : 'default' ;
KwEnum                  : 'enum' ;
KwEyes                  : 'eyes' ;
KwFacing                : 'facing' ;
KwFeet                  : 'feet' ;
KwFor                   : 'for' ;
KwIn                    : 'in' ;
KwIterator              : 'iterator' ;
KwPositioned            : 'positioned' ;
KwRotated               : 'rotated' ;
KwUse                   : 'use' ;
KwUser                  : 'user' ;

// Always reserved keywords
KwConst                 : 'const' ;
KwDefer                 : 'defer' ;
KwElse                  : 'else' ;
KwFalse                 : 'false' ;
KwFunc                  : 'func' ;
KwIf                    : 'if' ;
KwIs                    : 'is' ;
KwMatches               : 'matches' ;
KwPrivate               : 'private' ;
KwPublic                : 'public' ;
KwReturn                : 'return' ;
KwSwitch                : 'switch' ;
KwTrue                  : 'true' ;
KwVal                   : 'val' ;
KwVar                   : 'var' ;
KwYield                 : 'yield' ;

/*
 * Identifier
 */

fragment
IdentifierTail
    : ( Letter | '_' | Number09 )+
    ;

Identifier
    : ( Letter | '_' ) IdentifierTail?
    ;

/*
 * Whitespace,
 * Comments
 */

Whitespace
    : [ \r\n\t]+ -> skip
    ;

CommentLineSingle
    : '//' ~[\r\n]* -> skip
    ;

CommentLineMultipleStart
    : '/*' -> skip
    ;

CommentLineMultipleEnd
    : '*/' -> skip
    ;

CommentLineMultiple
    : CommentLineMultipleStart .*? CommentLineMultipleEnd -> skip
    ;
