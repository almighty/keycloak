/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.authorization.common;

import org.keycloak.authorization.attribute.Attributes;
import org.keycloak.authorization.identity.Identity;
import org.keycloak.authorization.policy.evaluation.EvaluationContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class KeycloakEvaluationContext implements EvaluationContext {

    private final KeycloakIdentity identity;
    private final KeycloakSession keycloakSession;

    public KeycloakEvaluationContext(KeycloakSession keycloakSession) {
        this(new KeycloakIdentity(keycloakSession), keycloakSession);
    }

    public KeycloakEvaluationContext(KeycloakIdentity identity, KeycloakSession keycloakSession) {
        this.identity = identity;
        this.keycloakSession = keycloakSession;
    }

    @Override
    public Identity getIdentity() {
        return this.identity;
    }

    @Override
    public Attributes getAttributes() {
        HashMap<String, Collection<String>> attributes = new HashMap<>();

        attributes.put("kc.time.date_time", Arrays.asList(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date())));
        attributes.put("kc.client.network.ip_address", Arrays.asList(this.keycloakSession.getContext().getConnection().getRemoteAddr()));
        attributes.put("kc.client.network.host", Arrays.asList(this.keycloakSession.getContext().getConnection().getRemoteHost()));

        AccessToken accessToken = this.identity.getAccessToken();

        if (accessToken != null) {
            attributes.put("kc.client.id", Arrays.asList(accessToken.getIssuedFor()));
        }

        List<String> userAgents = this.keycloakSession.getContext().getRequestHeaders().getRequestHeader("User-Agent");

        if (userAgents != null) {
            attributes.put("kc.client.user_agent", userAgents);
        }

        attributes.put("kc.realm.name", Arrays.asList(this.keycloakSession.getContext().getRealm().getName()));

        return Attributes.from(attributes);
    }
}
