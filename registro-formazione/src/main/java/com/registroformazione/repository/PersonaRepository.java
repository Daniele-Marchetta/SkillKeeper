package com.registroformazione.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.registroformazione.model.Persona;
import com.registroformazione.model.QPersona;

public interface PersonaRepository extends JpaRepository<Persona, Integer>,
	  QuerydslPredicateExecutor<Persona>, QuerydslBinderCustomizer<QPersona>{
		    @Override
		    public default void customize(
		      QuerydslBindings bindings, QPersona root) {
		        bindings.bind(String.class)
		          .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
		      }
		    
}
