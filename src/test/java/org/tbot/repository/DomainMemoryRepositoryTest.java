package org.tbot.repository;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.tbot.repository.impl.MemorySetRepository;
import org.junit.Assert;
import org.junit.Test;
import org.tbot.fetch.ProtocolType;
import org.tbot.objects.Domain;

/**
 *
 */
public class DomainMemoryRepositoryTest {

    public DomainMemoryRepositoryTest() {
    }

    @Test
    public void testSimpleInsert() {
        QueueRepository<Domain> domainRepo = new MemorySetRepository(10);
        Domain d = Domain.buildDomain(ProtocolType.Http, "testdomain");
        domainRepo.insert(d);
        Domain result = domainRepo.getNext();
        Assert.assertEquals(d, result);
    }

    @Test
    public void testBatchInsert() {
//        int capacity = 10;
//        QueueRepository<Domain> domainRepo = new MemorySetRepository(capacity);
//        Set<Domain> domains = new HashSet<>(capacity);
//        for (int i = 0; i < capacity; i++) {
//            Domain d = Domain.buildDomain(ProtocolType.Http, "testdomain" + i);
//            domains.add(d);
//        }
//        Collection<Domain> resultList = new LinkedList<>();
//        domainRepo.batchInsert(domains);
//        for (int i = 0; i < 10; i++) {
//            Domain res = domainRepo.getNext();
//            Assert.assertTrue(domains.contains(res));
//            resultList.add(res);
//        }
//        Assert.assertEquals(capacity, resultList.size());
//        Assert.assertEquals(null, domainRepo.getNext());
    }

    @Test
    public void testSameDomains() {
        int capacity = 10;
        QueueRepository<Domain> domainRepo = new MemorySetRepository(capacity);
        Domain d = Domain.buildDomain(ProtocolType.Http, "testdomain");
        domainRepo.insert(d);
        domainRepo.insert(d);
        Domain result = domainRepo.getNext();
        Assert.assertEquals(d, result);
        result = domainRepo.getNext();
        Assert.assertEquals(null, result);
    }

}
