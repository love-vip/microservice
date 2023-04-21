package com.vip.microservice.oauth2.support.security.core.authority;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

/**
 * @author echo
 * @date 2023-03-27 13:00:02
 */
public class UrlGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String url;

    public UrlGrantedAuthority(String url) {
        Assert.hasText(url, "A granted authority textual representation is required");
        this.url = url;
    }

    @Override
    public String getAuthority() {
        return this.url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof UrlGrantedAuthority) {
            return this.url.equals(((UrlGrantedAuthority) obj).url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    @Override
    public String toString() {
        return this.url;
    }

}

