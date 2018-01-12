package org.embulk.generic.core;

import org.embulk.generic.core.model.ExecutionContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Created by tai.khuu on 1/12/18.
 */
public class ELParser
{

    private static ELParser INSTANCE;

    private ExpressionParser expressionParser;

    private ELParser()
    {
        expressionParser = new SpelExpressionParser();
    }

    public static ELParser getInstance()
    {
        if (INSTANCE == null) {
            synchronized (INSTANCE) {
                if (INSTANCE == null) {
                    INSTANCE = new ELParser();
                }
            }
        }
        return INSTANCE;
    }
    public <T,R> T eval(String inputString, ExecutionContext context, R input, Class<T> klass){
        Expression expression = expressionParser.parseExpression(inputString);
        context.put("input", input);
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        return expression.getValue(evaluationContext, klass);
    }
}
