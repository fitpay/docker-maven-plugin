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

package net.wouterdanes.docker.remoteapi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * See <a href="http://docs.docker.io/reference/api/docker_remote_api_v1.10/#21-containers">
 * http://docs.docker.io/reference/api/docker_remote_api_v1.10/#create-a-container</a>
 */
@SuppressWarnings("unused")
public class ContainerCreateRequest {

    @JsonProperty("Hostname")
    private String hostname;
    @JsonProperty("User")
    private String user;
    @JsonProperty("Memory")
    private Long memory;
    @JsonProperty("Cmd")
    private List<String> cmd;
    @JsonProperty("Image")
    private String image;

    public String getHostname() {
        return hostname;
    }

    public String getUser() {
        return user;
    }

    public Long getMemory() {
        return memory;
    }

    public List<String> getCmd() {
        return cmd;
    }

    public String getImage() {
        return image;
    }

    public ContainerCreateRequest withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public ContainerCreateRequest withUser(String user) {
        this.user = user;
        return this;
    }

    public ContainerCreateRequest withMemory(long memory) {
        this.memory = memory;
        return this;
    }

    public ContainerCreateRequest withCommand(String command) {
        this.cmd = Arrays.asList(command);
        return this;
    }

    public ContainerCreateRequest withCommands(List<String> commands) {
        this.cmd = new ArrayList<>(commands);
        return this;
    }

    public ContainerCreateRequest fromImage(String image) {
        this.image = image;
        return this;
    }

}
