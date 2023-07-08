package com.registroformazione.filters.predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.registroformazione.filters.SearchCriteria;
import com.registroformazione.model.Persona;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonaPredicate {
	  private SearchCriteria criteria;


		public BooleanExpression getPredicate() {
	        PathBuilder<Persona> entityPath = new PathBuilder<>(Persona.class, "persona");

	        if (isNumeric(criteria.getValue().toString())) {
	            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
	            int value = Integer.parseInt(criteria.getValue().toString());
	            switch (criteria.getOperation()) {
	                case ":":
	                    return path.eq(value);
	                case ">":
	                    return path.goe(value);
	                case "<":
	                    return path.loe(value);
	            }
	        } 
	        else {
	            StringPath path = entityPath.getString(criteria.getKey());
	            if (criteria.getOperation().equalsIgnoreCase(":")) {
	                return path.containsIgnoreCase(criteria.getValue().toString());
	            }
	        }
	        return null;
	    }


		private boolean isNumeric(String string) {
	        try {
	            Integer.parseInt(string);
	        } catch (final NumberFormatException e) {
	            return false;
	        }
	        return true;
		}
}
