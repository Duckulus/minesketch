package de.duckulus.minesketch.interpreter;

import de.duckulus.minesketch.Parser;
import de.duckulus.minesketch.Scanner;
import de.duckulus.minesketch.ast.Expr;
import de.duckulus.minesketch.ast.Expr.ArrayAssignExpr;
import de.duckulus.minesketch.ast.Expr.ArrayExpr;
import de.duckulus.minesketch.ast.Expr.AssignExpr;
import de.duckulus.minesketch.ast.Expr.BinaryExpr;
import de.duckulus.minesketch.ast.Expr.CallExpr;
import de.duckulus.minesketch.ast.Expr.IndexExpr;
import de.duckulus.minesketch.ast.Expr.LiteralExpr;
import de.duckulus.minesketch.ast.Expr.LogicalExpr;
import de.duckulus.minesketch.ast.Expr.UnaryExpr;
import de.duckulus.minesketch.ast.Expr.VariableExpr;
import de.duckulus.minesketch.ast.Stmt;
import de.duckulus.minesketch.ast.Stmt.BlockStmt;
import de.duckulus.minesketch.ast.Stmt.ExprStmt;
import de.duckulus.minesketch.ast.Stmt.FunDeclaration;
import de.duckulus.minesketch.ast.Stmt.IfStmt;
import de.duckulus.minesketch.ast.Stmt.ReturnStmt;
import de.duckulus.minesketch.ast.Stmt.VarDeclaration;
import de.duckulus.minesketch.ast.Stmt.WhileStmt;
import de.duckulus.minesketch.token.Token;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

  private Environment environment = new Environment();
  private Map<String, Object> inputValues = new HashMap<>();

  public Interpreter() {
    addBuiltinFunction("println", new BuiltinFunction(1, args -> {
      System.out.println(args.getFirst());
      return null;
    }));

    addBuiltinFunction("array", new BuiltinFunction(1, args -> {
      if (args.getFirst() instanceof Integer capacity) {
        return new Array(capacity);
      } else {
        throw new RuntimeException("Expected argument of type int but found " + args.getFirst());
      }
    }));

    addBuiltinFunction("length", new BuiltinFunction(1, args -> switch (args.getFirst()) {
      case String s -> s.length();
      case Array arr -> arr.length();
      default -> throw new RuntimeException("Function length can not be applied to this type");
    }));
  }

  public void addBuiltinFunction(String name, BuiltinFunction function) {
    environment.define(name, function);
  }

  public void setInputValue(String name, Object value) {
    inputValues.put(name, value);
  }

  public void interpret(String text) {
    Scanner scanner = new Scanner(text);

    List<Token> tokens = scanner.scan();

    Parser parser = new Parser(tokens);

    List<Stmt> statements = parser.parse();

    for (Stmt statement : statements) {
      execute(statement);
    }
  }

  public Object invokeFunction(String name, List<Object> args) {
    if (!environment.hasValue(name)) {
      throw new RuntimeException("Object " + name + " not found");
    }
    Object object = environment.getValue(name);
    if (!(object instanceof Function function)) {
      throw new RuntimeException("Object " + object + " is not callable");
    }
    return function.call(this, args);
  }

  public void executeBlock(List<Stmt> statements, Environment environment) {
    Environment old = this.environment;
    try {
      this.environment = environment;
      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = old;
    }
  }

  public void execute(Stmt statement) {
    switch (statement) {
      case FunDeclaration funDeclaration -> environment.define(funDeclaration.identifier().lexeme(),
          new DefinedFunction(funDeclaration, environment));
      case VarDeclaration varDeclaration -> {
        if (varDeclaration.isInput()) {
          if (inputValues.containsKey(varDeclaration.identifier().lexeme())) {
            environment.define(varDeclaration.identifier().lexeme(),
                inputValues.get(varDeclaration.identifier().lexeme()));
          } else {
            throw new RuntimeException(
                "Input Variable " + varDeclaration.identifier().lexeme() + " not defined");
          }
        } else {
          environment.define(varDeclaration.identifier().lexeme(),
              evaluate(varDeclaration.value()));
        }
      }
      case ReturnStmt(Expr expr) -> throw new Return(evaluate(expr));
      case BlockStmt blockStmt ->
          executeBlock(blockStmt.statements(), new Environment(environment));
      case IfStmt ifStmt -> {
        if (isTruthy(evaluate(ifStmt.condition()))) {
          executeBlock(ifStmt.body().statements(), new Environment(environment));
        } else if (ifStmt.elseBlock() != null) {
          executeBlock(ifStmt.elseBlock().statements(), new Environment(environment));
        }
      }
      case ExprStmt exprStmt -> evaluate(exprStmt.expr());
      case WhileStmt whileStmt -> {
        Expr condition = whileStmt.condition();
        while (isTruthy(evaluate(condition)) && !Thread.currentThread().isInterrupted()) {
          executeBlock(whileStmt.body().statements(), new Environment(environment));
        }
      }
    }
  }

  public Object evaluate(Expr expression) {
    return switch (expression) {
      case LiteralExpr literalExpr -> literalExpr.value();
      case BinaryExpr(Expr leftExpr, Token operator, Expr rightExpr) -> {
        Object left = evaluate(leftExpr);
        Object right = evaluate(rightExpr);
        yield switch (operator.type()) {
          case PLUS:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 + i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 + d2;
            } else if (left instanceof String s1 && right instanceof String s2) {
              yield s1 + s2;
            } else if (left instanceof String s) {
              yield s + right.toString();
            } else if (right instanceof String s) {
              yield left.toString() + s;
            }
          case MINUS:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 - i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 - d2;
            }
          case STAR:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 * i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 * d2;
            }
          case SLASH:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 / i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 / d2;
            }
          case MODULO:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 % i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 + d2;
            }
          case GREATER:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 > i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 > d2;
            }
          case GREATER_EQUAL:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 >= i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 >= d2;
            }
          case LESS:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 < i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 <= d2;
            }
          case LESS_EQUAL:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1 <= i2;
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1 <= d2;
            }
          case BANG_EQUAL:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield !i1.equals(i2);
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield !d1.equals(d2);
            }
          case EQUAL_EQUAL:
            if (left instanceof Integer i1 && right instanceof Integer i2) {
              yield i1.equals(i2);
            } else if (left instanceof Double d1 && right instanceof Double d2) {
              yield d1.equals(d2);
            }
          default:
            throw new RuntimeException("Line " + operator.line() + ": Operator " + operator.type()
                + " can't be applied to the supplied arguments");
        };
      }
      case AssignExpr assignExpr -> {
        Object value = evaluate(assignExpr.value());
        environment.assign(assignExpr.name().lexeme(), value);
        yield value;
      }
      case CallExpr callExpr -> {
        if (evaluate(callExpr.callee()) instanceof Function fun) {
          List<Expr> args = callExpr.args();
          if (args.size() == fun.arity()) {
            yield fun.call(this, args.stream().map(this::evaluate).toList());
          } else {
            throw new RuntimeException(fun + " expected " + fun.arity() +
                " arguments but got " + args.size());
          }
        } else {
          throw new RuntimeException(callExpr.callee() + " is not callable");
        }
      }
      case VariableExpr variableExpr -> {
        String name = variableExpr.variable().lexeme();
        if (environment.hasValue(name)) {
          yield environment.getValue(name);
        } else {
          throw new RuntimeException(
              "Line " + variableExpr.variable().line() + ": Undefined Variable " + name);
        }
      }
      case LogicalExpr(Expr left, Token operator, Expr right) -> switch (operator.type()) {
        case AND -> isTruthy(evaluate(left)) && isTruthy(evaluate(right));
        case OR -> isTruthy(evaluate(left)) || isTruthy(evaluate(right));
        default -> throw new RuntimeException("Invalid logical operator " + operator.type());
      };
      case UnaryExpr unaryExpr -> {
        Object operand = evaluate(unaryExpr.operand());
        yield switch (unaryExpr.operator().type()) {
          case BANG:
            yield !isTruthy(operand);
          case MINUS:
            if (operand instanceof Double d) {
              yield -d;
            } else if (operand instanceof Integer i) {
              yield -i;
            }
          default:
            throw new RuntimeException(
                "Line " + unaryExpr.operator().line() + ": Unary Operator " + unaryExpr.operator()
                    .type() + " can't be applied to the supplied arguments");
        };
      }
      case IndexExpr indexExpr -> {
        if (evaluate(indexExpr.indexed()) instanceof Array arr) {
          if (evaluate(indexExpr.index()) instanceof Integer index) {
            yield arr.get(index);
          } else {
            throw new RuntimeException(indexExpr.index() + "can't be used as index");
          }
        } else {
          throw new RuntimeException(indexExpr.indexed() + " can't be indexed");
        }
      }
      case ArrayAssignExpr arrayAssignExpr -> {
        if (evaluate(arrayAssignExpr.name().indexed()) instanceof Array arr) {
          if (evaluate(arrayAssignExpr.name().index()) instanceof Integer index) {
            Object object = evaluate(arrayAssignExpr.value());
            arr.set(index, object);
            yield object;
          } else {
            throw new RuntimeException(arrayAssignExpr.name().index() + " can't be used as index");
          }
        } else {
          throw new RuntimeException(arrayAssignExpr.name().indexed() + " can't be indexed");
        }
      }
      case ArrayExpr(List<Expr> elements) ->
          Array.of(elements.stream().map(this::evaluate).toList());
    };
  }

  private boolean isTruthy(Object object) {
    return switch (object) {
      case null -> false;
      case Integer i -> i != 0;
      case Boolean bool -> bool;
      default -> true;
    };
  }

  public void clearEnvironment() {
    environment.clear();
  }

}
