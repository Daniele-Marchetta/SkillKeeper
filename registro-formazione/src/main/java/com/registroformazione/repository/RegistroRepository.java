package com.registroformazione.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;
import org.springframework.data.repository.query.Param;

import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.registroformazione.dto.ExcelValidationErrorsDto;
import com.registroformazione.model.ExcelValidationErrors;
import com.registroformazione.model.QRegistro;
import com.registroformazione.model.Registro;
import com.registroformazione.model.RegistroQueryResult;

import jakarta.transaction.Transactional;

public interface RegistroRepository extends JpaRepository<Registro, Integer>, QuerydslPredicateExecutor<Registro>,
        QuerydslBinderCustomizer<QRegistro> {
    @Override
    public default void customize(QuerydslBindings bindings, QRegistro root) {
        bindings.bind(String.class)
                .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
    }

    @Query(value = """
            SELECT a.nome AS attività, ARRAY_AGG(p.nome || ' ' || p.cognome) AS completato_da
            FROM Registro r
            JOIN Attivita a ON r.attivita=a.id
            JOIN Persona p ON r.persona=p.id
            JOIN Vendor v on a.vendor=v.id
            WHERE r.tipo = 'Certificazione' AND r.stato = 1 AND p.deleted=false AND v.nome=:vendor
            GROUP BY a.nome
            """)
    public List<Object[]> getTable(String vendor);

    @Query(value = """
            SELECT NEW com.registroformazione.model.RegistroQueryResult(ar.nome as Area, CAST(r.anno AS STRING) as Anno,
            CASE WHEN p.inForza = false THEN 'NO' ELSE '' END as `In Forza`, c.nome as Cc, v.nome as Vendor, co.nome as Competenza,
            p.cognome as Cognome, p.nome as Nome, CONCAT(p.nome, ' ', p.cognome) as `Nome completo`, r.tipo as Tipo, at.nome as Attività,
            s.nome as Stato, at.codice as Codice, CAST(r.dataCompletamento AS STRING) as `Data pianificato o eseguito`, CAST(r.dataScadenza AS STRING) as Scadenza,
            CASE WHEN CURRENT_DATE > dataScadenza THEN 'Scaduta' ELSE CAST(DATE_PART('year', age(dataScadenza, CURRENT_DATE)) * 12 +
            DATE_PART('month', age(dataScadenza, CURRENT_DATE)) AS STRING) END AS `Mesi a scadere`, r.nota as Note)
                  FROM Registro r
                  JOIN Persona p on r.persona=p.id
                  JOIN Area ar ON  p.area=ar.id
                  JOIN Cc c ON p.cc=c.id
                  JOIN Attivita at ON r.attivita = at.id
                  LEFT JOIN Vendor v ON at.vendor=v.id
                  LEFT JOIN Competenza co ON at.competenza=co.id
                  JOIN Stato s ON r.stato=s.id
                  WHERE r.deleted = false
                  """)
    public List<RegistroQueryResult> getRegistri();

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO public.staging_table
            ("area", anno, in_forza, cc, vendor, competenza, nome, cognome, nome_completo, tipo, attivita, stato, codice, data_pianificato_o_eseguito, scadenza, mesi_a_scadere, nota)
            VALUES(:#{#riga.area},:#{#riga.anno}, :#{#riga.inForza}, :#{#riga.cc}, :#{#riga.vendor},:#{#riga.competenza}, :#{#riga.nome}, :#{#riga.cognome}, :#{#riga.nomeCompleto}, :#{#riga.tipo}, :#{#riga.attivita}, :#{#riga.stato}, :#{#riga.codice}, :#{#riga.dataPianificatoOEseguito}, :#{#riga.scadenza}, :#{#riga.mesiAScadere}, :#{#riga.nota});
            """, nativeQuery = true)
    public void uploadLine(@Param("riga") RegistroQueryResult riga);

    @Transactional
    @Modifying
    @Query(value = "CREATE TABLE IF NOT EXISTS staging_table (id SERIAL PRIMARY KEY, area VARCHAR(255), anno VARCHAR(255), in_forza VARCHAR(255), cc VARCHAR(255), vendor VARCHAR(255), competenza VARCHAR(255), nome VARCHAR(255), cognome VARCHAR(255), nome_completo VARCHAR(255), tipo VARCHAR(255), attivita VARCHAR(255), stato VARCHAR(255), codice VARCHAR(255), data_pianificato_o_eseguito VARCHAR(255), scadenza VARCHAR(255), mesi_a_scadere VARCHAR(255), nota VARCHAR(255))", nativeQuery = true)
    void createStagingTable();

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE TABLE staging_table", nativeQuery = true)
    void truncateStagingTable();
    
    @Transactional
    @Modifying
    @Query(value = "ALTER SEQUENCE staging_table_id_seq RESTART WITH 2", nativeQuery = true)
    void restartSequence();
    
    
}
