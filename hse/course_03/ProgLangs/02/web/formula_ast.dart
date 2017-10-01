import 'dart:math';
import 'table.dart';

abstract class Expression {
  Value compute();
}

class Value implements Expression {
  final String string;
  final num number;

  Value(this.string, this.number);
  Value.forString(String str): this(str, null);
  Value.forNumber(num number): this(null, number);

  bool get isString => number == null;
  bool get isNumber => string == null;
  @override
  Value compute() {
    return this;
  }
}

class BinaryExpression implements Expression {
  final BinaryOperation operation;
  final Expression leftOperand;
  final Expression rightOperand;

  BinaryExpression(this.operation, this.leftOperand, this.rightOperand);

  @override
  Value compute() {
    final leftValue = leftOperand.compute();
    final rightValue = rightOperand.compute();

    if (leftValue.isString && rightValue.isNumber ||
        leftValue.isNumber && rightValue.isString) {
      throw new Exception("Binary operators are not defined for"
          "operands with different types");
    }

    if (leftValue.isString && rightValue.isString) {
      if (operation == BinaryOperation.PLUS) {
        return new Value.forString(leftValue.string + rightValue.string);
      } else {
        throw new Exception("Only string concatenation operation supported.");
      }
    }

    final leftNumber = leftValue.number;
    final rightNumber = rightValue.number;

    switch (operation) {
      case BinaryOperation.PLUS: return new Value.forNumber(leftNumber + rightNumber);
      case BinaryOperation.MINUS: return new Value.forNumber(leftNumber - rightNumber);
      case BinaryOperation.MULTIPLY: return new Value.forNumber(leftNumber * rightNumber);
      case BinaryOperation.DIVIDE: return new Value.forNumber(leftNumber.toDouble() / rightNumber);
    }

    throw new Error();
  }
}

class UnaryExpression implements Expression {
  final UnaryOperation operation;
  final Expression operand;

  UnaryExpression(this.operation, this.operand);


  @override
  Value compute() {
    final operandValue = operand.compute();
    if (operandValue.isString) {
      throw new Exception("Unary operations are not supported for strings");
    }

    return operation == UnaryOperation.PLUS ?
      operandValue :
      new Value.forNumber(-operandValue.number);
  }
}

class FunctionCallExpression implements Expression {
  final Functions function;
  final List<Expression> arguments;


  FunctionCallExpression(this.function, this.arguments);
  factory FunctionCallExpression.forName(String funcName, List<Expression> args) {
    switch (funcName) {
      case 'SIN': return new FunctionCallExpression(Functions.SIN, args);
      case 'ABS': return new FunctionCallExpression(Functions.ABS, args);
      case 'LEN': return new FunctionCallExpression(Functions.LEN, args);
      default:
        throw new Exception("unsupported function: '$funcName'");
    }
  }

  @override
  Value compute() {
    // check arguments

    final List<Value> args = arguments.map((exp) => exp.compute()).toList();
    switch (function) {
      case Functions.SIN:
      case Functions.ABS:
        if (args.length != 1 || args[0].isString) {
          throw new Exception("Function $function: expected one numeric argument,"
              " but got ${args.length} arguments of types: "
              "${args.map((arg) => arg.isString ? "string" : "number")
                          .join(", ")}");
        }
        break;
      case Functions.LEN:
        if (args.length != 1 || args[0].isNumber) {
          throw new Exception("Function $function: expected one string argument,"
              " but got ${args.length} arguments of types: "
              "${args.map((arg) => arg.isString ? "string" : "number")
              .join(", ")}");
        }
    }

    final Value value = args[0];
    switch (function) {
      case Functions.SIN: return new Value.forNumber(sin(value.number));
      case Functions.ABS: return new Value.forNumber(
          value.number > 0 ? value.number : -value.number);
      case Functions.LEN: return new Value.forNumber(value.string.length);
    }

    throw new Error();
  }
}

class CellDereferenceExpression implements Expression {
  final String cellName;

  CellDereferenceExpression(this.cellName);


  @override
  Value compute() {
    final cell = TABLE_CONTROLLER.cells[cellName];
    if (cell == null) {
      throw new Exception("There is no cell $cellName");
    } else if (cell.value == null) {
      throw new Exception("cell $cellName is empty. Can not dereference it");
    }
    return cell.value;
  }
}

enum BinaryOperation {
  PLUS,
  MINUS,
  MULTIPLY,
  DIVIDE,
}

enum UnaryOperation {
  PLUS,
  MINUS,
}

enum Functions {
  SIN,
  ABS,
  LEN,
}
