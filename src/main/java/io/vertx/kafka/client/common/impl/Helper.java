/*
 * Copyright 2016 Red Hat Inc.
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

package io.vertx.kafka.client.common.impl;

import io.vertx.core.Handler;
import io.vertx.kafka.client.common.Node;
import io.vertx.kafka.client.consumer.OffsetAndTimestamp;
import io.vertx.kafka.client.common.TopicPartition;
import io.vertx.kafka.client.consumer.OffsetAndMetadata;
import io.vertx.kafka.client.producer.RecordMetadata;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class for mapping native and Vert.x Kafka objects
 */
public class Helper {

  private Helper() {
  }

  public static <T> Set<T> toSet(Collection<T> collection) {
    if (collection instanceof Set) {
      return (Set<T>) collection;
    } else {
      return new HashSet<>(collection);
    }
  }

  public static org.apache.kafka.common.TopicPartition to(TopicPartition topicPartition) {
    return new org.apache.kafka.common.TopicPartition(topicPartition.getTopic(), topicPartition.getPartition());
  }

  public static Set<org.apache.kafka.common.TopicPartition> to(Set<TopicPartition> topicPartitions) {
    return topicPartitions.stream().map(Helper::to).collect(Collectors.toSet());
  }

  public static Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> to(Map<TopicPartition, OffsetAndMetadata> offsets) {
    return offsets.entrySet().stream().collect(Collectors.toMap(
      e -> new org.apache.kafka.common.TopicPartition(e.getKey().getTopic(), e.getKey().getPartition()),
      e -> new org.apache.kafka.clients.consumer.OffsetAndMetadata(e.getValue().getOffset(), e.getValue().getMetadata()))
    );
  }

  public static Map<TopicPartition, OffsetAndMetadata> from(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
    return offsets.entrySet().stream().collect(Collectors.toMap(
      e -> new TopicPartition(e.getKey().topic(), e.getKey().partition()),
      e -> new OffsetAndMetadata(e.getValue().offset(), e.getValue().metadata()))
    );
  }

  public static TopicPartition from(org.apache.kafka.common.TopicPartition topicPartition) {
    return new TopicPartition(topicPartition.topic(), topicPartition.partition());
  }

  public static Set<TopicPartition> from(Collection<org.apache.kafka.common.TopicPartition> topicPartitions) {
    return topicPartitions.stream().map(Helper::from).collect(Collectors.toSet());
  }

  public static Handler<Set<org.apache.kafka.common.TopicPartition>> adaptHandler(Handler<Set<TopicPartition>> handler) {
    if (handler != null) {
      return topicPartitions -> handler.handle(Helper.from(topicPartitions));
    } else {
      return null;
    }
  }

  public static Node from(org.apache.kafka.common.Node node) {
    return new Node(node.hasRack(), node.host(), node.id(), node.idString(),
      node.isEmpty(), node.port(), node.rack());
  }

  public static RecordMetadata from(org.apache.kafka.clients.producer.RecordMetadata metadata) {
    return new RecordMetadata(metadata.checksum(), metadata.offset(),
      metadata.partition(), metadata.timestamp(), metadata.topic());
  }

  public static OffsetAndMetadata from(org.apache.kafka.clients.consumer.OffsetAndMetadata offsetAndMetadata) {
    return new OffsetAndMetadata(offsetAndMetadata.offset(), offsetAndMetadata.metadata());
  }

  public static org.apache.kafka.clients.consumer.OffsetAndMetadata to(OffsetAndMetadata offsetAndMetadata) {
    return new org.apache.kafka.clients.consumer.OffsetAndMetadata(offsetAndMetadata.getOffset(), offsetAndMetadata.getMetadata());
  }

  public static Map<TopicPartition, Long> fromTopicPartitionOffsets(Map<org.apache.kafka.common.TopicPartition, Long> offsets) {
    return offsets.entrySet().stream().collect(Collectors.toMap(
      e -> new TopicPartition(e.getKey().topic(), e.getKey().partition()),
      Map.Entry::getValue)
    );
  }

  public static Map<org.apache.kafka.common.TopicPartition, Long> toTopicPartitionTimes(Map<TopicPartition, Long> topicPartitionTimes) {
    return topicPartitionTimes.entrySet().stream().collect(Collectors.toMap(
      e -> new org.apache.kafka.common.TopicPartition(e.getKey().getTopic(), e.getKey().getPartition()),
      Map.Entry::getValue)
    );
  }

  public static Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndTimestamp> toTopicPartitionOffsetAndTimestamp(Map<TopicPartition, OffsetAndTimestamp> topicPartitionOffsetAndTimestamps) {
    return topicPartitionOffsetAndTimestamps.entrySet().stream().collect(Collectors.toMap(
      e -> new org.apache.kafka.common.TopicPartition(e.getKey().getTopic(), e.getKey().getPartition()),
      e -> new org.apache.kafka.clients.consumer.OffsetAndTimestamp(e.getValue().getOffset(), e.getValue().getTimestamp()))
    );
  }

  public static Map<TopicPartition, OffsetAndTimestamp> fromTopicPartitionOffsetAndTimestamp(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndTimestamp> topicPartitionOffsetAndTimestamps) {
    return topicPartitionOffsetAndTimestamps.entrySet().stream().collect(Collectors.toMap(
      e -> new TopicPartition(e.getKey().topic(), e.getKey().partition()),
      e -> new OffsetAndTimestamp(e.getValue().offset(), e.getValue().timestamp()))
    );
  }
}
