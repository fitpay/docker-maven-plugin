/*
    Copyright 2014 Wouter Danes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

*/

package net.wouterdanes.docker.remoteapi;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import com.google.common.io.BaseEncoding;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import net.wouterdanes.docker.remoteapi.exception.DockerException;
import net.wouterdanes.docker.remoteapi.exception.ImageNotFoundException;
import net.wouterdanes.docker.remoteapi.model.Credentials;

/**
 * This class is responsible for holding the shared functionality of all Docker remoteapi services.
 */
public abstract class BaseService {

    public static final String TARGET_DOCKER_API_VERSION = "v1.12";
    public static final String REGISTRY_AUTH_HEADER = "X-Registry-Auth";

    // required for "push" even if no credentials required
    private static final String REGISTRY_AUTH_NULL_VALUE = "null";

    private final ObjectMapper objectMapper;
    private final WebTarget serviceEndPoint;
    private Credentials credentials = null;

    public BaseService(String dockerApiRoot, String endPointPath) {
        objectMapper = new ObjectMapper();
        // Only send properties that are actually set, default values are often wrong
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        // If the API changes, we might get new properties that we do not know
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig()
                .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setDeserializationConfig(deserializationConfig);
        serviceEndPoint = ClientBuilder.newClient()
                .target(dockerApiRoot)
                .path(TARGET_DOCKER_API_VERSION)
                .path(endPointPath);
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    protected WebTarget getServiceEndPoint() {
        return serviceEndPoint;
    }

    protected String getRegistryAuthHeaderValue() {
        if (credentials == null) {
            return REGISTRY_AUTH_NULL_VALUE;
        }
        return BaseEncoding.base64().encode(toJson(credentials).getBytes());
    }

    protected String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to Jsonify", e);
        }
    }

    protected <T> T toObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot convert Json", e);
        }
    }

    protected static void checkImageTargetingResponse(final String id, final Response.StatusType statusInfo) {
        if (statusInfo.getFamily() == Family.SUCCESSFUL) {
            // no error
            return;
        }

        switch (statusInfo.getStatusCode()) {
            case 404:
                throw new ImageNotFoundException(id);
            default:
                throw new DockerException(statusInfo.getReasonPhrase());
        }
    }

    protected static DockerException makeImageTargetingException(final String id, final WebApplicationException cause) {
        Response.StatusType statusInfo = cause.getResponse().getStatusInfo();
        switch (statusInfo.getStatusCode()) {
            case 404:
                return new ImageNotFoundException(id, cause);
            default:
                return new DockerException(statusInfo.getReasonPhrase(), cause);
        }
    }
}
