/*
 * This file is generated by jOOQ.
 */
package io.en4ble.examples.jooq.tables.records;


import io.en4ble.examples.enums.ConferenceState;
import io.en4ble.examples.jooq.tables.ConferenceV1;
import io.en4ble.pgaccess.dto.PointDTO;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@io.swagger.v3.oas.annotations.media.Schema(name="ConferenceV1")
public class ConferenceV1Record extends TableRecordImpl<ConferenceV1Record> implements Serializable, Cloneable, Record7<UUID, String, String, LocalDate, LocalDate, PointDTO, ConferenceState> {

    private static final long serialVersionUID = 224156497;

    /**
     * Setter for <code>pgaccess.conference_v1.id</code>.
     */
    public ConferenceV1Record setId(UUID value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.id</code>.
     */
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="id")
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.name</code>.
     */
    public ConferenceV1Record setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.name</code>.
     */
    @org.hibernate.validator.constraints.Length(max = 30)
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="name")
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.about</code>.
     */
    public ConferenceV1Record setAbout(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.about</code>.
     */
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="about")
    public String getAbout() {
        return (String) get(2);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.start_date</code>.
     */
    public ConferenceV1Record setStartDate(LocalDate value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.start_date</code>.
     */
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="startDate")
    public LocalDate getStartDate() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.end_date</code>.
     */
    public ConferenceV1Record setEndDate(LocalDate value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.end_date</code>.
     */
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="endDate")
    public LocalDate getEndDate() {
        return (LocalDate) get(4);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.location</code>.
     */
    public ConferenceV1Record setLocation(PointDTO value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.location</code>.
     */
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="location")
    public PointDTO getLocation() {
        return (PointDTO) get(5);
    }

    /**
     * Setter for <code>pgaccess.conference_v1.state</code>.
     */
    public ConferenceV1Record setState(ConferenceState value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>pgaccess.conference_v1.state</code>.
     */
    @org.hibernate.validator.constraints.Length(max = 2)
    @io.swagger.v3.oas.annotations.media.Schema(accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_WRITE, name="state")
    public ConferenceState getState() {
        return (ConferenceState) get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<UUID, String, String, LocalDate, LocalDate, PointDTO, ConferenceState> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<UUID, String, String, LocalDate, LocalDate, PointDTO, ConferenceState> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UUID> field1() {
        return ConferenceV1.CONFERENCE_V1.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ConferenceV1.CONFERENCE_V1.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ConferenceV1.CONFERENCE_V1.ABOUT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field4() {
        return ConferenceV1.CONFERENCE_V1.START_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field5() {
        return ConferenceV1.CONFERENCE_V1.END_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<PointDTO> field6() {
        return ConferenceV1.CONFERENCE_V1.LOCATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<ConferenceState> field7() {
        return ConferenceV1.CONFERENCE_V1.STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getAbout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate component4() {
        return getStartDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate component5() {
        return getEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointDTO component6() {
        return getLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceState component7() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getAbout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value4() {
        return getStartDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value5() {
        return getEndDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointDTO value6() {
        return getLocation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceState value7() {
        return getState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value1(UUID value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value3(String value) {
        setAbout(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value4(LocalDate value) {
        setStartDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value5(LocalDate value) {
        setEndDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value6(PointDTO value) {
        setLocation(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record value7(ConferenceState value) {
        setState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConferenceV1Record values(UUID value1, String value2, String value3, LocalDate value4, LocalDate value5, PointDTO value6, ConferenceState value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ConferenceV1Record
     */
    public ConferenceV1Record() {
        super(ConferenceV1.CONFERENCE_V1);
    }

    /**
     * Create a detached, initialised ConferenceV1Record
     */
    public ConferenceV1Record(UUID id, String name, String about, LocalDate startDate, LocalDate endDate, PointDTO location, ConferenceState state) {
        super(ConferenceV1.CONFERENCE_V1);

        set(0, id);
        set(1, name);
        set(2, about);
        set(3, startDate);
        set(4, endDate);
        set(5, location);
        set(6, state);
    }
}
