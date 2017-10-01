import 'dart:html';
import 'formula_ast.dart';
import 'formula_parser.dart';

TableController TABLE_CONTROLLER;

class MyTableCell {
  final TableCellElement cell;

  final List<MyTableCell> dependents = new List<MyTableCell>();
  final List<MyTableCell> usedCells = new List<MyTableCell>();

  String formula = "";

  Value __value__ = new Value.forString("");
  void set value(Value value) {
    print('value setter called for value $value');
    this.__value__ = value;
    if (value == null) {
      cell.text = "";
    } else {

      cell.text = value.isString ? value.string : value.number.toString();
    }
  }
  Value get value {
    return __value__;
  }

  String __error__ = null;
  void set error(String error) {
    this.__error__   = error;
    if (error != null && error != "") {
      cell.text = error;
    }
  }
  String get error {
    return __error__;
  }

  MyTableCell(this.cell) {
    cell.onFocus.listen((_) => cell.text = formula);
    cell.onBlur.listen((_) => computeAndShow());
    computeAndShow();
  }

  void computeAndShow([bool recompute=false]) {
    print("computing value for cell ${cell.id}");
    _compute(recompute);
    dependents.forEach((cell) => cell.computeAndShow(true));
  }

  void _compute([bool recompute=false]) {
    if (!recompute) {
      formula = cell.text;
    }
    print('formula : $formula');
    if (formula == "") {
      value = new Value.forString("");
      error = null;
    } else if (formula[0] != '=') {
      final number = num.parse(formula, (_) => null);
      value = number != null ?
          new Value.forNumber(number) :
          new Value.forString(formula);
      error = null;
    } else {
      print('parsing formula');
      Parser parser;
      try {
        parser = new Parser(formula.substring(1));
        print('parsed successfully');
      } catch (e) {
        print('failed to parse formula: $e');
        value = null;
        error = e.toString();
        usedCells.clear();
        return;
      }
      final Expression exp = parser.getExpression();
      _updateUsedCellsList(exp);

      try {
        if (exp is BinaryExpression) {
          print('computing binary expression ${exp.leftOperand} ${exp.operation} ${exp.rightOperand}');
        }
        value = exp.compute();
        error = "";
      } catch(e) {
        value = null;
        error = e.toString();
    }
    }
  }

  void _updateUsedCellsList(Expression exp) {
    usedCells.forEach((cell) => cell.dependents.remove(this));
    usedCells.clear();

    List<Expression> expressions = [exp];
    while (!expressions.isEmpty) {
      final cur = expressions.removeLast();
      if (cur is CellDereferenceExpression) {
        final usedCell = TABLE_CONTROLLER.cells[cur.cellName];
        if (usedCell != null) {
          usedCells.add(usedCell);
          usedCell.dependents.add(this);
        }
      } else if (cur is BinaryExpression) {
        expressions..add(cur.leftOperand)..add(cur.rightOperand);
      } else if (cur is UnaryExpression) {
        expressions.add(cur.operand);
      } else if (cur is FunctionCallExpression) {
        cur.arguments.forEach(expressions.add);
      }
    }
  }
}

class TableController {
  final Map<String, MyTableCell> cells;

  TableController(this.cells);
}

TableController generateTable() {
  final TableElement table = querySelector('#table');

  final head = table.createTHead();
  final columnNames = head.addRow()..id = 'columnNames';
  columnNames.addCell(); // empty corner cell
  for (int column = 'A'.codeUnitAt(0); column <= 'Z'.codeUnitAt(0); column++) {
    columnNames.addCell().text = new String.fromCharCode(column);
  }

  final body = table.createTBody();
  final Map<String, MyTableCell> cells = new Map<String, MyTableCell>();
  for (int line = 1; line <= 20; line++) {
    final currentLine = body.addRow();
    currentLine.addCell()
      ..text = line.toString()
      ..contentEditable = false;
    for (int column = 'A'.codeUnitAt(0); column <= 'Z'.codeUnitAt(0); column++) {
      final cellName = new String.fromCharCode(column) + line.toString();
      final cell = currentLine.addCell()
        ..id = cellName
        ..contentEditable = true;
      cells[cellName] = new MyTableCell(cell);
    }
  }
  return new TableController(cells);
}
