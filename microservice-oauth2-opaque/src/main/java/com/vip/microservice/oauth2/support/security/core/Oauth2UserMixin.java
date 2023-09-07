package com.vip.microservice.oauth2.support.security.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.vip.microservice.oauth2.support.security.core.authority.UrlGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = Oauth2UserMixin.CustomUserDeserializer.class)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface Oauth2UserMixin {

    class CustomUserDeserializer extends JsonDeserializer<User> {

        private static final TypeReference<Set<UrlGrantedAuthority>> SIMPLE_GRANTED_AUTHORITY_SET = new TypeReference<Set<UrlGrantedAuthority>>() {};

        /**
         * @param jp   the JsonParser
         * @param ctx the DeserializationContext
         * @return the user
         * @throws IOException             if a exception during IO occurs
         * @throws JsonProcessingException if an error during JSON processing occurs
         */
        @Override
        public User deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
            ObjectMapper mapper = (ObjectMapper) jp.getCodec();
            JsonNode jsonNode = mapper.readTree(jp);
            Set<? extends GrantedAuthority> authorities = JsonNodeUtils.findValue(jsonNode, "authorities", SIMPLE_GRANTED_AUTHORITY_SET, mapper);
            JsonNode passwordNode = JsonNodeUtils.findObjectNode(jsonNode, "password");
            String username = JsonNodeUtils.findStringValue(jsonNode, "username");
            String password = passwordNode.asText("");
            boolean enabled = JsonNodeUtils.findObjectNode(jsonNode, "enabled").asBoolean();
            boolean accountNonExpired = JsonNodeUtils.findObjectNode(jsonNode, "accountNonExpired").asBoolean();
            boolean credentialsNonExpired = JsonNodeUtils.findObjectNode(jsonNode, "credentialsNonExpired").asBoolean();
            boolean accountNonLocked = JsonNodeUtils.findObjectNode(jsonNode, "accountNonLocked").asBoolean();
            User result = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
            if (passwordNode.asText(null) == null) {
                result.eraseCredentials();
            }
            return result;
        }

    }

    abstract class JsonNodeUtils {

        static final TypeReference<Set<String>> STRING_SET = new TypeReference<Set<String>>() {
        };

        static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP = new TypeReference<Map<String, Object>>() {
        };

        static String findStringValue(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isTextual()) ? value.asText() : null;
        }

        static <T> T findValue(JsonNode jsonNode, String fieldName, TypeReference<T> valueTypeReference, ObjectMapper mapper) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isContainerNode()) ? mapper.convertValue(value, valueTypeReference) : null;
        }

        static JsonNode findObjectNode(JsonNode jsonNode, String fieldName) {
            if (jsonNode == null) {
                return null;
            }
            JsonNode value = jsonNode.findValue(fieldName);
            return (value != null && value.isObject()) ? value : null;
        }

    }

}
