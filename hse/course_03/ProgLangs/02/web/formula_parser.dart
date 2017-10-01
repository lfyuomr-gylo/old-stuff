import 'formula_ast.dart';

class Parser {
  // ---------------- Tokens
  static final RegExp WHITESPACES = new RegExp(r"\s*");
  static final RegExp IDENTIFIER = new RegExp(r"[A-Z_][A-Z\d_]*");
  static final RegExp NUMBER = new RegExp(r"([+-]?\d+(\.\d+)?)");
  static final RegExp STRING_LITERAL = new RegExp(r'"([^"]*)"');
  // ----------------/Tokens

  final String str;

  int _offset = 0;
  Expression _result;
  Expression getExpression() => _result;

  Parser(this.str) {
    _findNextToken();
    _result = _offset < str.length ? _expression() : null;
  }

  /*
    expression ::= additiveExpression

    additiveExpression ::=
        multiplicativeExpression ["+" additiveExpression] |
        multiplicativeExpression ["-" additiveExpression]

    multiplicativeExpression ::=
        unaryExpression ["*" multiplicativeExpression]
        unaryExpression ["/" multiplicativeExpression]

    unaryExpression ::=
        expressionElement |
        "+" expressionElement
        "-" expressionElement

    expressionElement ::=
        value |
        "(" expression ")" |
        functionCall |
        cellDereference

   value ::= NUMBER | STRING_LITERAL

   functionCall ::=
      IDENTIFIER "(" ")" |
      IDENTIFIER "(" expressionList ")"

   expressionList ::=
      expression ("," expression)*

    cellDereference ::=
       "$" IDENTIFIER
   */

  Expression _expression() {
    _findNextToken();
    print('searching for expression at $_offset');
    return _additiveExpression();
  }

  Expression _additiveExpression() {
    _findNextToken();
    print('searching for additive expression at $_offset');

    final Expression leftOperand = _multiplicativeExpression();
    print('found left operand in additive');


    _findNextToken();
    String operator;
    if (_offset < str.length &&
        ((operator = str[_offset]) == '+' ||
        (operator = str[_offset]) == '-')) {
      _offset++;
      _findNextToken();

      final Expression rightOperand = _additiveExpression();
      return operator == '+' ?
        new BinaryExpression(BinaryOperation.PLUS, leftOperand, rightOperand) :
        new BinaryExpression(BinaryOperation.MINUS, leftOperand, rightOperand);
    } else {
      return leftOperand;
    }
  }

  Expression _multiplicativeExpression() {
    _findNextToken();
    print('searching for multiplicative expression at $_offset');

    final Expression leftOperand = _unaryExpression();
    print('found left operand in multiplicative');
    _findNextToken();
    String operator;
    if (_offset < str.length &&
        ((operator = str[_offset]) == '*' ||
        (operator = str[_offset]) == '/')) {
      _offset++;
      _findNextToken();

      final Expression rightOperand = _multiplicativeExpression();
      return operator == '*' ?
        new BinaryExpression(BinaryOperation.MULTIPLY, leftOperand, rightOperand) :
        new BinaryExpression(BinaryOperation.DIVIDE, leftOperand, rightOperand);
    } else {
      return leftOperand;
    }
  }

  Expression _unaryExpression() {
    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. Unary expression was expected");
    }
    print('searching unary expression at $_offset');

    String operator;
    if ((operator = str[_offset]) == '+' ||
        (operator = str[_offset]) == '-') {
      _offset++;
      _findNextToken();

      final Expression element = _expressionElement();
      return operator == '+' ?
        new UnaryExpression(UnaryOperation.PLUS, element) :
        new UnaryExpression(UnaryOperation.MINUS, element);
    } else {
      return _expressionElement();
    }
  }

  Expression _expressionElement() {
    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. Expression element"
          "(value, function call, cell dereference or expression in parentheses)"
          " was expected");
    }
    print('looking for expression element at $_offset');

    Match match;
    if ((match = STRING_LITERAL.matchAsPrefix(str, _offset)) != null) { // value
      _offset = match.end;
      return new Value.forString(match.group(1));
    } else if ((match = NUMBER.matchAsPrefix(str, _offset)) != null) { // value
      _offset = match.end;
      print('found expression element ${num.parse(str.substring(match.start, match.end))}');
      return new Value.forNumber(num.parse(str.substring(match.start, match.end)));
    } else if (str[_offset] == '(') { // "(" expression ")"
      _offset++;
      _findNextToken();
      final Expression expression = _expression();
      _findNextToken();
      if (_offset >= str.length) {
        throw new Exception("unexpected end of line. ')' was expected");
      } else if (str[_offset] != ')') {
        throw new Exception("unexpected character '${str[_offset]}'. "
            "')' was expected");
      } else {
        _offset++;
        return expression;
      }
    } else if (str[_offset] == '\$') { // cell dereference
      print("dollat is seen");
      _offset++;
      _findNextToken();

      match = IDENTIFIER.matchAsPrefix(str, _offset);
      if (match == null) {
        throw new Exception("unexpected token at position $_offset. "
            "Cell name was expected");
      }
      _offset = match.end;
      return new CellDereferenceExpression(str.substring(match.start, match.end));
    } else { // function call
      return _functionCall();
    }
  }

  Expression _functionCall() {
    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. Function call was expected");
    }

    final Match funcNameMatch = IDENTIFIER.matchAsPrefix(str, _offset);
    if (funcNameMatch == null) {
      throw new Exception("unexpected token at position $_offset. "
          "Function name was expected");
    }
    final String funcName = str.substring(funcNameMatch.start, funcNameMatch.end);
    _offset = funcNameMatch.end;
    _findNextToken();

    // read '('
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. '(' was expected");
    } else if (str[_offset] != '(') {
      throw new Exception("unexpected character ${str[_offset]} at position "
          "$_offset. '(' was expected");
    }
    _offset++;
    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. ')' or argument list "
          "was expected");
    }

    if (str[_offset] == ')') { // empty argument list
      _offset++;
      return new FunctionCallExpression.forName(funcName, []);
    }

    final List<Expression> args = _expressionList();

    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpeted end of line. ')' was expected");
    } else if (str[_offset] != ')') {
      throw new Exception("unexpected charaqcter ${str[_offset]} at position "
          "$_offset. ')' was expected");
    } else {
      _offset++;
      return new FunctionCallExpression.forName(funcName, args);
    }
  }

  List<Expression> _expressionList() {
    _findNextToken();
    if (_offset >= str.length) {
      throw new Exception("unexpected end of line. Expression was expected");
    }

    final List<Expression> expressions = new List<Expression>();
    expressions.add(_expression());

    _findNextToken();
    while (_offset < str.length && str[_offset] == ',') {
      _offset++;
      _findNextToken();
      expressions.add(_expression());
      _findNextToken();
    }
    return expressions;
  }

  // skip whitespaces
  void _findNextToken() {
    final Match spaces = WHITESPACES.matchAsPrefix(str, _offset);
    if (spaces != null) {
      _offset = spaces.end;
    }
  }
}