/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License; you may not use this file
 * except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.rest.server.mock;

import io.confluent.ksql.rest.entity.CommandStatus;
import io.confluent.ksql.rest.entity.CommandStatuses;
import io.confluent.ksql.rest.server.computation.CommandId;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
public class MockStatusResource {
  Map<CommandId,CommandStatus.Status> statuses;

  public MockStatusResource() {
    statuses = new
        HashMap<>();
    statuses.put(new CommandId(CommandId.Type.TOPIC, "c1", CommandId.Action.CREATE), CommandStatus.Status.SUCCESS);
    statuses.put(new CommandId(CommandId.Type.TOPIC, "c2", CommandId.Action.CREATE), CommandStatus.Status.ERROR);
  }

  @GET
  public Response getAllStatuses() {
    final CommandStatuses commandStatuses = new CommandStatuses(statuses);
    return Response.ok(commandStatuses).build();
  }

  @GET
  @Path("/{type}/{entity}/{action}")
  public Response getStatus(@PathParam("type") final String type,
                            @PathParam("entity") final String entity,
                            @PathParam("action") final String action) {
    final CommandStatus.Status status = statuses.get(new CommandId(type, entity, action));
    if (status == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    return Response.ok(new CommandStatus(status, "")).build();
  }
}
