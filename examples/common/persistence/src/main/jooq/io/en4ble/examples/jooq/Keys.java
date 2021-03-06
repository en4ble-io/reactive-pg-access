/*
 * This file is generated by jOOQ.
 */
package io.en4ble.examples.jooq;


import io.en4ble.examples.jooq.tables.Conference;
import io.en4ble.examples.jooq.tables.Example;
import io.en4ble.examples.jooq.tables.records.ConferenceRecord;
import io.en4ble.examples.jooq.tables.records.ExampleRecord;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>pgaccess</code> schema.
 */
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<ExampleRecord, Short> IDENTITY_EXAMPLE = Identities0.IDENTITY_EXAMPLE;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<ConferenceRecord> C_T_CONFERENCE_PK = UniqueKeys0.C_T_CONFERENCE_PK;
    public static final UniqueKey<ExampleRecord> E_P_EXAMPLE_PK = UniqueKeys0.E_P_EXAMPLE_PK;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<ExampleRecord, Short> IDENTITY_EXAMPLE = Internal.createIdentity(Example.EXAMPLE, Example.EXAMPLE.SHORT_SERIAL);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<ConferenceRecord> C_T_CONFERENCE_PK = Internal.createUniqueKey(Conference.CONFERENCE, "t_conference_pk", Conference.CONFERENCE.ID);
        public static final UniqueKey<ExampleRecord> E_P_EXAMPLE_PK = Internal.createUniqueKey(Example.EXAMPLE, "p_example_pk", Example.EXAMPLE.UUID);
    }
}
