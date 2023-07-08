package com.registroformazione.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.registroformazione.model.ExcelImportErrorLog;
import com.registroformazione.repository.ExcelRepository;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    private JdbcTemplate jdbcTemplate;

    public ExcelRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int saveAree() {
        String query = "insert into public.aree\r\n"
                + "(nome)\r\n"
                + "select distinct st.area\r\n"
                + "from staging_table st\r\n"
                + "left join aree a on a.nome = st.area\r\n"
                + "where a.id is null";
        return jdbcTemplate.update(query);   
    }

    @Override
    public int saveStati() {
        String query = "insert into public.stati \r\n"
                + "(nome)\r\n"
                + "select distinct st.stato\r\n"
                + "from staging_table st\r\n"
                + "left join stati s on s.nome = st.stato\r\n"
                + "where s.id is null\r\n"
                + "";
        return jdbcTemplate.update(query);  
    }

    @Override
    public int saveCc() {
        String query = "insert into public.cc\r\n"
                + "(nome)\r\n"
                + "select distinct st.cc\r\n"
                + "from staging_table st\r\n"
                + "left join cc c on c.nome = st.cc\r\n"
                + "where c.id is null";
        return jdbcTemplate.update(query);  
    }

    @Override
    public int saveVendors() {
        String query = "insert into public.vendors\r\n"
                + "(nome)\r\n"
                + "select distinct st.vendor\r\n"
                + "from staging_table st\r\n"
                + "left join vendors v on v.nome = st.vendor\r\n"
                + "where v.id is null and st.vendor is not null";
        return jdbcTemplate.update(query);  
    }

    @Override
    public int saveCompetenze() {
        String query = "insert into public.competenze \r\n"
                + "(nome)\r\n"
                + "select distinct st.competenza\r\n"
                + "from staging_table st\r\n"
                + "left join competenze c on c.nome = st.competenza\r\n"
                + "where c.id is null and st.competenza is not null";
        return jdbcTemplate.update(query); 
    }

    @Override
    public int savePersone() {
        String query = "INSERT INTO public.persone\r\n"
                + "(nome, cognome,cc_id, in_forza,area_id, deleted)\r\n"
                + "select distinct st.nome, st.cognome, cc.id,\r\n"
                + "case when st.in_forza is null then true\r\n"
                + "when st.in_forza = 'NO' then false\r\n"
                + "end as in_forza, aree.id, false from staging_table st \r\n"
                + "join cc on st.cc=cc.nome\r\n"
                + "join aree on st.area=aree.nome\r\n"
                + "left join persone p on p.cc_id = cc.id and p.area_id = aree.id and p.nome=st.nome and p.cognome=st.cognome\r\n"
                + "where p.id is null";
        return jdbcTemplate.update(query); 
    }

    @Override
    public int saveAttivita() {
        String query = "insert into public.attivita \r\n"
                + "(nome, codice, vendor_id, competenza_id)\r\n"
                + "select distinct st.attivita, st.codice, v.id, c.id from staging_table st\r\n"
                + "left join vendors v on st.vendor=v.nome\r\n"
                + "left join competenze c on st.competenza=c.nome\r\n"
                + "left join attivita a on a.nome=st.attivita and (a.codice=st.codice or (a.codice is null and st.codice is null)) and ( a.vendor_id=v.id or (a.vendor_id is null and v.id is null)) and (a.competenza_id=c.id or (a.competenza_id is null and c.id is null))\r\n"
                + "where a.id is null";
        return jdbcTemplate.update(query); 
    }

    @Override
    public int saveRegistro() {
        String query = "insert into public.registro\r\n"
                + "(tipo, nota, data_completamento, data_scadenza, persona_id, attivita_id, anno, stato_id, deleted)\r\n"
                + "select st.tipo, st.nota, TO_DATE(st.data_pianificato_o_eseguito, 'YYYY-MM-DD'),\r\n"
                + "case when st.scadenza != '' THEN TO_DATE(st.scadenza, 'YYYY-MM-DD') else null end, p.id, a.id, cast(st.anno as integer), s.id, false \r\n"
                + "from staging_table st\r\n"
                + "join cc on st.cc=cc.nome\r\n"
                + "join aree on st.area=aree.nome\r\n"
                + "left join vendors v on st.vendor=v.nome\r\n"
                + "left join competenze c on st.competenza=c.nome\r\n"
                + "join persone p on p.nome=st.nome and p.cognome=st.cognome and p.cc_id=cc.id and p.area_id=aree.id\r\n"
                + "left join attivita a on a.nome=st.attivita and (a.codice=st.codice or (a.codice is null and st.codice is null)) and ( a.vendor_id=v.id or (a.vendor_id is null and v.id is null)) and (a.competenza_id=c.id or (a.competenza_id is null and c.id is null))\r\n"
                + "join stati s on s.nome=st.stato\r\n"
                + "left join registro r on r.tipo=st.tipo and (r.nota=st.nota or (r.nota is null and st.nota is null)) and r.data_completamento=TO_DATE(st.data_pianificato_o_eseguito, 'YYYY-MM-DD') \r\n"
                + "and (r.data_scadenza=TO_DATE(st.scadenza, 'YYYY-MM-DD') or (r.data_scadenza is null and st.scadenza is null)) and r.persona_id=p.id and r.attivita_id=a.id and r.anno=cast(st.anno as integer) and r.stato_id=s.id\r\n"
                + "where r.id is null";
        return jdbcTemplate.update(query); 
    }

    public Integer checkState(String stato){
        String query ="SELECT MAX(id) FROM excel_import_error_logs WHERE stato = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, stato);    }

}
