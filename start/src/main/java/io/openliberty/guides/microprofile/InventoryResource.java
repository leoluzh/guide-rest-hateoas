// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::comment[]
package io.openliberty.guides.microprofile;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

// tag::header[]
// tag::cdi-scope[]
@ApplicationScoped
// end::cdi-scope[]
@Path("hosts")
public class InventoryResource {
// end::header[]

    // tag::injection[]
    @Inject
    InventoryManager manager;
    // end::injection[]

    @Context
    UriInfo uriInfo;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject handler() {
    	return manager.getSystems(uriInfo.getAbsolutePath().toString());
    }
    
    // tag::getPropertiesForHost[]
    @GET
    @Path("{hostname}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getPropertiesForHost(@PathParam("hostname") String hostname) {
        // tag::method-contents[]
        return ("*".equals(hostname)) ? manager.list() : manager.get(hostname);
        // end::method-contents[]
    }
    // end::getPropertiesForHost[]


}
