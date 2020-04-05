create table p_example
(
    e_uuid               uuid not null
        constraint p_example_pk
            primary key,
    e_uuid_array         uuid array,

    e_text               text,
    e_text_array         text array,
    e_string             varchar(20),
    e_string_array       varchar(20) array,

    -- numbers
    e_numeric_value      numeric,
    e_numeric_array      numeric[],
    e_short_value        smallint,
    e_short_array        smallint[],
    e_integer_value      integer,
    e_integer_array      integer[],
    e_long_value         bigint,
    e_long_array         bigint[],
    e_float_value        real,
    e_float_array        real[],
    e_double_value       double precision,
    e_double_array       double precision[],
    e_boolean_value      boolean,
    e_boolean_array      boolean[],
    e_byte_value         bytea,
    e_byte_array         bytea[],

    -- date + time
    e_date               date,
    e_date_array         date[],
    e_time               time,
    e_time_array         time[],
    e_timetz             timetz,
    e_timetz_array       timetz[],
    e_date_time          timestamp,
    e_date_time_array    timestamp[],
    e_date_timetz        timestamptz,
    e_date_timetz_array  timestamptz[],

    -- json
    e_json               json,
    e_json_array         json[],
    e_jsonb              jsonb,
    e_jsonb_array        jsonb[],

    -- geometric types
    e_point              point,
    e_point_array        point[],
    e_line               line,
    e_line_array         line[],
    e_line_segment       lseg,
    e_line_segment_array lseg[],
    e_box                box,
    e_box_array          box[],
    e_path               path,
    e_path_array         path[],
    e_polygon            polygon,
    e_polygon_array      polygon[],
    e_circle             circle,
    e_circle_array       circle[],

    e_name               name,
    e_name_array         name array,
    e_interval           interval,
    e_interval_array     interval[],

    e_short_serial       serial2,
    e_serial             serial4,
    e_long_serial        serial8

);

comment on table p_example is 'An example table containing all datatypes that are supported by reactive-pg-access.';

comment on column p_example.e_text is '{{minLength=10}}{{maxLength=200}}';

comment on column p_example.e_string is '{{minLength=4}}';

alter table p_example
    owner to postgres;

