package com.registroformazione.filters.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.registroformazione.filters.SearchCriteria;
import com.registroformazione.filters.predicates.RegistroPredicate;

public class RegistroPredicatesBuilder {
    private List<SearchCriteria> params;

    public RegistroPredicatesBuilder() {
        params = new ArrayList<>();
    }

    public RegistroPredicatesBuilder with(
      final String key,final String operation,final Object value) {
  
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.isEmpty()) {
            return null;
        }
        
        final List<BooleanExpression> predicates = params.stream().map(param -> {
            RegistroPredicate predicate = new RegistroPredicate(param);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).toList();
        
        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        
        return result;
    }
    static class BooleanExpressionWrapper {
        
        private BooleanExpression result;

        public BooleanExpressionWrapper(final BooleanExpression result) {
            super();
            this.result = result;
        }
        
        public BooleanExpression getResult() {
            return result;
        }
        public void setResult(BooleanExpression result) {
            this.result = result;
        }
    }
}
