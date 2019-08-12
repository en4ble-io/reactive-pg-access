/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jooq.impl;

import io.en4ble.pgaccess.dto.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.data.*;
import io.vertx.sqlclient.data.Numeric;
import org.jooq.DataType;

/**
 *
 */
public final class PgClientSQLDataType {
    public static final DataType<Numeric> NUMERIC = new DefaultDataType<>(null, Numeric.class, "numeric");
    public static final DataType<Buffer> BYTEA = new DefaultDataType<>(null, Buffer.class, "bytea");

    public static final DataType<JsonObject> JSON = new DefaultDataType<>(null, JsonObject.class, "json");
    public static final DataType<JsonObject> JSONB = new DefaultDataType<>(null, JsonObject.class, "jsonb");

    public static final DataType<Point> POINT = new DefaultDataType<>(null, Point.class, "point");
    public static final DataType<PointDTO> POINT_DTO = new DefaultDataType<>(null, PointDTO.class, "point");
    public static final DataType<Line> LINE = new DefaultDataType<>(null, Line.class, "line");
    public static final DataType<LineDTO> LINE_DTO = new DefaultDataType<>(null, LineDTO.class, "line");
    public static final DataType<LineSegment> LSEG = new DefaultDataType<>(null, LineSegment.class, "lseg");
    public static final DataType<LineSegmentDTO> LSEG_DTO = new DefaultDataType<>(null, LineSegmentDTO.class, "lseg");
    public static final DataType<Box> BOX = new DefaultDataType<>(null, Box.class, "box");
    public static final DataType<BoxDTO> BOX_DTO = new DefaultDataType<>(null, BoxDTO.class, "box");
    public static final DataType<Path> PATH = new DefaultDataType<>(null, Path.class, "path");
    public static final DataType<PathDTO> PATH_DTO = new DefaultDataType<>(null, PathDTO.class, "path");
    public static final DataType<Polygon> POLYGON = new DefaultDataType<>(null, Polygon.class, "polygon");
    public static final DataType<PolygonDTO> POLYGON_DTO = new DefaultDataType<>(null, PolygonDTO.class, "polygon");
    public static final DataType<Circle> CIRCLE = new DefaultDataType<>(null, Circle.class, "circle");
    public static final DataType<CircleDTO> CIRCLE_DTO = new DefaultDataType<>(null, CircleDTO.class, "circle");
    public static final DataType<Interval> INTERVAL = new DefaultDataType<>(null, Interval.class, "interval");
    public static final DataType<IntervalDTO> INTERVAL_DTO = new DefaultDataType<>(null, IntervalDTO.class, "interval");


    /**
     * No instances
     */
    private PgClientSQLDataType() {
    }
}
