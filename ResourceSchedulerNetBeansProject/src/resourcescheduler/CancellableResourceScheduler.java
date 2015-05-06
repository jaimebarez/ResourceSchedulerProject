/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcescheduler;

import java.util.Collection;
import java.util.HashSet;
import resourcescheduler.model.gateway.CompleteGateway;
import resourcescheduler.model.message.Message;

/**
 *
 * @author jaime.barez.lobato
 */
public class CancellableResourceScheduler extends ResourceScheduler {

    final Collection<Long> cancelledGroups;

    public CancellableResourceScheduler(CompleteGateway gateway) {
        super(gateway);
        cancelledGroups = new HashSet<>();
    }

    @Override
    protected void internalReceiveMessage(Message msg, long groupId) {
        if (!cancelledGroups.contains(groupId)) {
            super.internalReceiveMessage(msg, groupId);
        }
    }

    public void addCancellableGroup(long groupId) {
        cancelledGroups.add(groupId);
    }
}
