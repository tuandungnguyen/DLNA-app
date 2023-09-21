package com.android.cast.dlna.core.http

import android.text.TextUtils
import com.android.cast.dlna.core.Logger
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.BAD_REQUEST
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import fi.iki.elonen.NanoHTTPD.Response.Status.SERVICE_UNAVAILABLE
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

// ------------------------------------------------
// ---- Jetty Http
// ------------------------------------------------
internal class JettyHttpServer(port: Int) : HttpServer {
    private val logger = Logger.create("JettyHttpServer")
    private val server: Server = Server(port) // Has its own QueuedThreadPool

    init {
        server.gracefulShutdown = 1000 // Let's wait a second for ongoing transfers to complete
    }

    @Synchronized
    override fun startServer() {
        if (!server.isStarted && !server.isStarting) {
            Thread {
                val context = ServletContextHandler()
                context.contextPath = "/"
                context.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false")
                // context.addServlet(ContentResourceServlet.AudioResourceServlet.class, "/audio/*");
                // context.addServlet(ContentResourceServlet.VideoResourceServlet.class, "/video/*");
                context.addServlet(ContentResourceServlet::class.java, "/")
                server.handler = context
                logger.i("JettyServer start.")
                try {
                    server.start()
                    server.join()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    logger.e("Error", ex)
                } finally {
                    logger.i("JettyServer complete.")
                }
            }.start()
        }
    }

    @Synchronized
    override fun stopServer() {
        if (!server.isStopped && !server.isStopping) {
            try {
                server.stop()
            } catch (ex: Exception) {
                logger.e("Error", ex)
                ex.printStackTrace()
            } finally {
                logger.i("JettyServer stop.")
            }
        }
    }

    override fun isRunning(): Boolean = server.isRunning
}

// ------------------------------------------------
// ---- Nano Http
// ------------------------------------------------
internal class NanoHttpServer(port: Int) : NanoHTTPD(port), HttpServer {

    private val mimeType = mutableMapOf(
        "jpg" to "image/*",
        "jpeg" to "image/*",
        "png" to "image/*",
        "mp3" to "audio/*",
        "mp4" to "video/*",
        "wav" to "video/*",
    )
    private val textPlain = "text/plain"

    override fun serve(session: IHTTPSession): Response {
        println("uri: " + session.uri)
        println("header: " + session.headers.toString())
        println("params: " + session.parms.toString())
        val uri = session.uri
        if (TextUtils.isEmpty(uri) || !uri.startsWith("/")) {
            return newChunkedResponse(BAD_REQUEST, textPlain, null)
        }
        val file = File(uri)
        if (!file.exists() || file.isDirectory) {
            return newChunkedResponse(NOT_FOUND, textPlain, null)
        }
        val type = uri.substring(uri.length.coerceAtMost(uri.lastIndexOf(".") + 1)).lowercase()
        var mimeType = mimeType[type]
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = textPlain
        }
        return try {
            newChunkedResponse(OK, mimeType, FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            newChunkedResponse(SERVICE_UNAVAILABLE, mimeType, null)
        }
    }

    override fun startServer() {
        try {
            if (!wasStarted()) start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun stopServer() {
        stop()
    }

    override fun isRunning(): Boolean = wasStarted()
}