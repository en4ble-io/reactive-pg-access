package io.en4ble.pgaccess.example

import io.reactivex.Completable
import io.reactivex.Single
import io.vertx.core.json.Json
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.reactivex.ext.web.Route
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler
import io.vertx.reactivex.ext.web.handler.ErrorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.UUID
import javax.validation.ValidationException
import javax.validation.Validator

/**
 *
 * @author Mark Hofmann (mark@en4ble.io)
 */
open class RouterBase(
    delegate: io.vertx.ext.web.Router,
    val validator: Validator
) : Router(delegate) {
    protected val LOG by lazy { LoggerFactory.getLogger(RouterBase::class.java) }

    init {
        route().handler(BodyHandler.create())
        route().failureHandler(ErrorHandler { ctx ->
            val failure = ctx.failure()
            val statusCode = if (failure is ValidationException) 422 else 500
            ctx.response().setStatusCode(statusCode).end(failure.message)
            LOG.error("exception in request", failure)
        })
    }

    protected fun getId(it: RoutingContext) = UUID.fromString(it.request().getParam("id"))

    protected fun <T> getForm(ctx: RoutingContext, type: Class<T>): T {
        val jsonString = ctx.getBodyAsString("UTF-8")
        val form: T
        try {
            LOG.debug("creating instance of type: {} from json: '{}'", type, jsonString)
            form = if (jsonString.isNullOrEmpty()) {
                LOG.debug("json does not contain data, returning empty instance.")
                type.newInstance()
            } else {
                Json.mapper.readValue(jsonString, type)
            }
        } catch (e: IOException) {
            LOG.warn("could not convert json: '{}' to type: {}", jsonString, type.name, e)
            throw ValidationException("invalid json", e)
        } catch (e: Exception) {
            LOG.error("could not convert json: '{}' to type: {}", jsonString, type.name, e)
            throw ValidationException(
                "could not get instance of form: $type from json: '$jsonString'", e
            )
        }

        val validationErrors = try {
            validator.validate(form)
        } catch (e: Exception) {
            LOG.error("error in validation", e)
            null
        }
        if (validationErrors != null && validationErrors.isNotEmpty()) {
            val error = validationErrors.iterator().next()
            throw ValidationException("$error.propertyPath $error.message")
        }

        return form
    }

    protected fun Route.cr(fn: suspend (RoutingContext) -> Any) {
        handler { ctx ->
            GlobalScope.launch(ctx.vertx().delegate.dispatcher()) {
                try {
                    ctx.response().end(Json.encode(fn(ctx)))
                } catch (e: Exception) {
                    ctx.fail(e)
                }
            }
        }
    }

    protected fun Route.rx(fn: suspend (RoutingContext) -> Any) {
        handler { ctx ->
            GlobalScope.launch(ctx.vertx().delegate.dispatcher()) {
                try {
                    when (val res = fn(ctx)) {
                        is Single<*> -> res.subscribe(
                            { ctx.response().end(Json.encode(it)) },
                            { ctx.fail(500, it) })
                        is Completable -> res.subscribe(
                            { ctx.response().end() },
                            { ctx.fail(500, it) })
                        else -> ctx.response().end(Json.encode(res))
                    }
                } catch (e: Exception) {
                    ctx.fail(e)
                }
            }
        }
    }
}
