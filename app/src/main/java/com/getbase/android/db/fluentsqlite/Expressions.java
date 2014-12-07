package com.getbase.android.db.fluentsqlite;

import com.getbase.android.db.fluentsqlite.Query.QueryBuilder;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Expressions {

  public static final Function<Query, Iterable<String>> GET_TABLES = new Function<Query, Iterable<String>>() {
    @Override
    public Iterable<String> apply(Query subquery) {
      return subquery.getTables();
    }
  };

  private Expressions() {
  }

  static void addExpressionArgs(List<Object> args, Expression expression, Object... boundArgs) {
    if (boundArgs == null) {
      Preconditions.checkArgument(
          expression.getArgsCount() == expression.getBoundArgs().size(),
          "Expression contains args placeholders, but bound args list is null"
      );
      args.addAll(expression.getBoundArgs().values());
    } else {
      Preconditions.checkArgument(
          expression.getArgsCount() == boundArgs.length + expression.getBoundArgs().size(),
          "Invalid number of arguments: expression has %s arg placeholders and %s bound args, so I need %s additional args specified, but there was %s args",
          expression.getArgsCount(),
          expression.getBoundArgs().size(),
          (expression.getArgsCount() - expression.getBoundArgs().size()),
          boundArgs.length
      );

      int boundArgsIndex = 0;
      for (int i = 0; i < expression.getArgsCount(); i++) {
        final Object arg;

        if (expression.getBoundArgs().containsKey(i)) {
          arg = expression.getBoundArgs().get(i);
        } else {
          arg = boundArgs[boundArgsIndex++];
        }

        args.add(arg);
      }
    }
  }

  public interface UnaryOperator {
    ExpressionCore not();
  }

  public static abstract class Expression {
    Expression() {
    }

    public String toRawSql() {
      Preconditions.checkState(getBoundArgs().isEmpty(), "Cannot get raw sql for Expression with bound args.");
      return getSql();
    }

    abstract String getSql();
    abstract int getArgsCount();
    abstract Map<Integer, Object> getBoundArgs();
    abstract Set<String> getTables();
    abstract Object[] getMergedArgs(Object... boundArgs);
  }

  public interface ExpressionCore {
    // basic stuff
    ExpressionCombiner column(String col);
    ExpressionCombiner column(String table, String col);
    ExpressionCombiner arg();
    ExpressionCombiner nul();
    ExpressionCombiner literal(Number number);
    ExpressionCombiner literal(Object object);

    // aggregate functions
    ExpressionCombiner sum(Expression e);
    ExpressionCombiner count(Expression e);
    ExpressionCombiner count();
    ExpressionCombiner max(Expression e);
    ExpressionCombiner min(Expression e);

    // coalescing functions
    ExpressionCombiner ifNull(Expression left, Expression right);
    ExpressionCombiner nullIf(Expression left, Expression right);
    ExpressionCombiner coalesce(Expression... expressions);

    // strings operations
    ExpressionCombiner length(Expression e);
    ExpressionCombiner concat(Expression... e);
    ExpressionCombiner join(String on, Expression... e);

    // generic expression
    ExpressionCombiner expr(String expression);
  }

  public interface CaseExpressions {
    CaseCondition cases();
    CaseCondition cases(Expression e);
  }

  public interface CaseCondition {
    CaseValue when(Expression e);
  }

  public interface CaseValue {
    CaseExpressionBuilder then(Expression e);
  }

  public interface CaseExpressionBuilder extends CaseCondition, CaseExpressionEndStep {
    ExpressionCombiner otherwise(Expression e);
  }

  public interface CaseExpressionEndStep {
    ExpressionCombiner end();
  }

  public interface BinaryOperator {
    ExpressionBuilder eq();
    ExpressionCombiner eq(Expression e);
    ExpressionBuilder ne();
    ExpressionCombiner ne(Expression e);
    ExpressionBuilder gt();
    ExpressionCombiner gt(Expression e);
    ExpressionBuilder ge();
    ExpressionCombiner ge(Expression e);
    ExpressionBuilder lt();
    ExpressionCombiner lt(Expression e);
    ExpressionBuilder le();
    ExpressionCombiner le(Expression e);

    ExpressionBuilder is();
    ExpressionCombiner is(Expression e);

    ExpressionCombiner in(Query subquery);
    ExpressionCombiner in(QueryBuilder subqueryBuilder);
    ExpressionCombiner in(Expression... e);

    ExpressionCombiner notIn(Query subquery);
    ExpressionCombiner notIn(QueryBuilder subqueryBuilder);
    ExpressionCombiner notIn(Expression... e);

    ExpressionBuilder or();
    ExpressionCombiner or(Expression e);
    ExpressionBuilder and();
    ExpressionCombiner and(Expression e);
  }

  public interface ExpressionBuilder extends UnaryOperator, ExpressionCore, CaseExpressions {
  }

  public static abstract class ExpressionCombiner extends Expression implements BinaryOperator {
  }

  // mirror all method from ExpressionBuilder interface
  public static ExpressionCore not() {
    return new Builder().not();
  }

  public static ExpressionCombiner column(String col) {
    return new Builder().column(col);
  }

  public static ExpressionCombiner column(String table, String col) {
    return new Builder().column(table, col);
  }

  public static ExpressionCombiner arg() {
    return new Builder().arg();
  }

  public static ExpressionCombiner nul() {
    return new Builder().nul();
  }

  public static ExpressionCombiner literal(Number number) {
    return new Builder().literal(number);
  }

  public static ExpressionCombiner literal(Object object) {
    return new Builder().literal(object);
  }

  public static Expression[] literals(Object... objects) {
    Preconditions.checkNotNull(objects);
    Expression[] result = new Expression[objects.length];

    for (int i = 0; i < objects.length; i++) {
      result[i] = literal(objects[i]);
    }

    return result;
  }

  public static Expression[] literals(Number... numbers) {
    Preconditions.checkNotNull(numbers);
    Expression[] result = new Expression[numbers.length];

    for (int i = 0; i < numbers.length; i++) {
      result[i] = literal(numbers[i]);
    }

    return result;
  }

  public static ExpressionCombiner sum(Expression e) {
    return new Builder().sum(e);
  }

  public static ExpressionCombiner count(Expression e) {
    return new Builder().count(e);
  }

  public static ExpressionCombiner count() {
    return new Builder().count();
  }

  public static ExpressionCombiner max(Expression e) {
    return new Builder().max(e);
  }

  public static ExpressionCombiner min(Expression e) {
    return new Builder().min(e);
  }

  public static ExpressionCombiner ifNull(Expression left, Expression right) {
    return new Builder().ifNull(left, right);
  }

  public static ExpressionCombiner nullIf(Expression left, Expression right) {
    return new Builder().nullIf(left, right);
  }

  public static ExpressionCombiner coalesce(Expression... expressions) {
    return new Builder().coalesce(expressions);
  }

  public static ExpressionCombiner length(Expression e) {
    return new Builder().length(e);
  }

  public static ExpressionCombiner concat(Expression... e) {
    return new Builder().concat(e);
  }

  public static ExpressionCombiner expr(String expression) {
    return new Builder().expr(expression);
  }

  public static ExpressionCombiner join(String on, Expression... e) {
    return new Builder().join(on, e);
  }

  public static CaseCondition cases() {
    return new Builder().cases();
  }

  public static CaseCondition cases(Expression e) {
    return new Builder().cases(e);
  }

  private static class Builder extends ExpressionCombiner implements ExpressionBuilder, CaseExpressionBuilder, CaseValue {
    private StringBuilder mBuilder = new StringBuilder();
    private Map<Integer, Object> mArgs = Maps.newHashMap();
    private List<Query> mSubqueries = Lists.newArrayList();
    private int mArgsCount;

    private static final Joiner ARGS_JOINER = Joiner.on(", ");
    private static final Joiner CONCAT_JOINER = Joiner.on(" || ");
    private static final Function<Expression, String> GET_EXPR_SQL = new Function<Expression, String>() {
      @Override
      public String apply(Expression e) {
        return e.getSql();
      }
    };

    private void expr(Expression... e) {
      for (Expression expression : e) {
        for (Entry<Integer, Object> boundArg : expression.getBoundArgs().entrySet()) {
          mArgs.put(mArgsCount + boundArg.getKey(), boundArg.getValue());
        }
        mArgsCount += expression.getArgsCount();
      }

      mBuilder
          .append("(")
          .append(ARGS_JOINER.join(getSQLs(e)))
          .append(")");
    }

    private ExpressionBuilder binaryOperator(String operator) {
      mBuilder.append(" ");
      mBuilder.append(operator);
      mBuilder.append(" ");
      return this;
    }

    @Override
    public ExpressionBuilder eq() {
      return binaryOperator("==");
    }

    @Override
    public ExpressionCombiner eq(Expression e) {
      eq();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder ne() {
      return binaryOperator("!=");
    }

    @Override
    public ExpressionCombiner ne(Expression e) {
      ne();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder gt() {
      return binaryOperator(">");
    }

    @Override
    public ExpressionCombiner gt(Expression e) {
      gt();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder ge() {
      return binaryOperator(">=");
    }

    @Override
    public ExpressionCombiner ge(Expression e) {
      ge();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder lt() {
      return binaryOperator("<");
    }

    @Override
    public ExpressionCombiner lt(Expression e) {
      lt();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder le() {
      return binaryOperator("<=");
    }

    @Override
    public ExpressionCombiner le(Expression e) {
      le();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder is() {
      return binaryOperator("IS");
    }

    @Override
    public ExpressionCombiner is(Expression e) {
      is();
      expr(e);
      return this;
    }

    @Override
    public ExpressionCombiner in(Query subquery) {
      RawQuery rawQuery = subquery.toRawQuery();
      for (String rawQueryArg : rawQuery.mRawQueryArgs) {
        mArgs.put(mArgsCount++, rawQueryArg);
      }
      mSubqueries.add(subquery);

      binaryOperator("IN");

      mBuilder
          .append("(")
          .append(rawQuery.mRawQuery)
          .append(")");

      return this;
    }

    @Override
    public ExpressionCombiner in(QueryBuilder subqueryBuilder) {
      return in(subqueryBuilder.build());
    }

    @Override
    public ExpressionCombiner in(Expression... e) {
      binaryOperator("IN");
      expr(e);
      return this;
    }

    @Override
    public ExpressionCombiner notIn(Query subquery) {
      mBuilder.append(" NOT");
      return in(subquery);
    }

    @Override
    public ExpressionCombiner notIn(QueryBuilder subqueryBuilder) {
      return notIn(subqueryBuilder.build());
    }

    @Override
    public ExpressionCombiner notIn(Expression... e) {
      mBuilder.append(" NOT");
      return in(e);
    }

    @Override
    public ExpressionBuilder or() {
      return binaryOperator("OR");
    }

    @Override
    public ExpressionCombiner or(Expression e) {
      or();
      expr(e);
      return this;
    }

    @Override
    public ExpressionBuilder and() {
      return binaryOperator("AND");
    }

    @Override
    public ExpressionCombiner and(Expression e) {
      and();
      expr(e);
      return this;
    }

    @Override
    public String getSql() {
      return mBuilder.toString().trim();
    }

    @Override
    public int getArgsCount() {
      return mArgsCount;
    }

    @Override
    public Map<Integer, Object> getBoundArgs() {
      return mArgs;
    }

    @Override
    public Set<String> getTables() {
      return FluentIterable
          .from(mSubqueries)
          .transformAndConcat(GET_TABLES)
          .toImmutableSet();
//           .toSet();
    }

    @Override
    public Object[] getMergedArgs(Object... boundArgs) {
      ArrayList<Object> args = Lists.newArrayList();
      addExpressionArgs(args, this, boundArgs);
      return args.toArray();
    }

    @Override
    public ExpressionCombiner column(String col) {
      mBuilder.append(col);
      return this;
    }

    @Override
    public ExpressionCombiner column(String table, String col) {
      mBuilder.append(table);
      mBuilder.append(".");
      mBuilder.append(col);
      return this;
    }

    @Override
    public ExpressionCombiner arg() {
      mBuilder.append("?");
      ++mArgsCount;
      return this;
    }

    @Override
    public ExpressionCombiner nul() {
      mBuilder.append("NULL");
      return this;
    }

    @Override
    public ExpressionCombiner literal(Number number) {
      mBuilder.append(number.toString());
      return this;
    }

    @Override
    public ExpressionCombiner literal(Object object) {
      mBuilder
          .append('\'')
          .append(object.toString().replaceAll("'", "''"))
          .append('\'');
      return this;
    }

    @Override
    public ExpressionCombiner sum(Expression e) {
      return function("SUM", e);
    }

    @Override
    public ExpressionCombiner count(Expression e) {
      return function("COUNT", e);
    }

    @Override
    public ExpressionCombiner count() {
      mBuilder.append("COUNT(*)");
      return this;
    }

    @Override
    public ExpressionCombiner max(Expression e) {
      return function("MAX", e);
    }

    @Override
    public ExpressionCombiner min(Expression e) {
      return function("MIN", e);
    }

    @Override
    public ExpressionCombiner ifNull(Expression left, Expression right) {
      return function("ifnull", left, right);
    }

    @Override
    public ExpressionCombiner nullIf(Expression left, Expression right) {
      return function("nullif", left, right);
    }

    @Override
    public ExpressionCombiner coalesce(Expression... expressions) {
      Preconditions.checkArgument(expressions.length >= 2);
      return function("coalesce", expressions);
    }

    @Override
    public ExpressionCombiner length(Expression e) {
      return function("length", e);
    }

    @Override
    public ExpressionCombiner concat(Expression... e) {
      mBuilder.append(CONCAT_JOINER.join(getSQLs(e)));
      return this;
    }

    private Iterable<String> getSQLs(Expression[] e) {
      return Iterables.transform(Arrays.asList(e), GET_EXPR_SQL);
    }

    private static <T> Iterable<T> intersperse(final T element, final Iterable<T> iterable) {
      return new Iterable<T>() {
        @Override
        public Iterator<T> iterator() {
          final Iterator<T> iterator = iterable.iterator();
          return new AbstractIterator<T>() {
            boolean intersperse = false;

            @Override
            protected T computeNext() {
              if (iterator.hasNext()) {
                final T result;
                if (intersperse) {
                  result = element;
                } else {
                  result = iterator.next();
                }
                intersperse = !intersperse;
                return result;
              }
              return endOfData();
            }
          };
        }
      };
    }

    @Override
    public ExpressionCombiner join(String on, Expression... e) {
      return concat(FluentIterable
          .from(
              intersperse(
                  Expressions.literal(on),
                  Arrays.asList(e)
              )
          )
          .toArray(Expression.class));
    }

    private ExpressionCombiner function(String func, Expression... e) {
      mBuilder.append(func);
      expr(e);
      return this;
    }

    @Override
    public ExpressionCombiner expr(String expr) {
      mBuilder.append(expr);
      return this;
    }

    @Override
    public ExpressionCore not() {
      mBuilder.append("NOT ");
      return this;
    }

    @Override
    public ExpressionCombiner otherwise(Expression e) {
      mBuilder.append(" ELSE ");
      expr(e);
      return end();
    }

    @Override
    public CaseValue when(Expression e) {
      mBuilder.append(" WHEN ");
      expr(e);
      return this;
    }

    @Override
    public ExpressionCombiner end() {
      mBuilder.append(" END");
      return this;
    }

    @Override
    public CaseCondition cases() {
      mBuilder.append("CASE");
      return this;
    }

    @Override
    public CaseCondition cases(Expression e) {
      mBuilder.append("CASE ");
      expr(e);
      return this;
    }

    @Override
    public CaseExpressionBuilder then(Expression e) {
      mBuilder.append(" THEN ");
      expr(e);
      return this;
    }
  }
}
