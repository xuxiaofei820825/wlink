// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wlink/SessionMessageProto.proto

package com.iauto.wlink.core.message.proto;

public final class SessionMessageProto {
  private SessionMessageProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface SessionMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.iauto.wlink.core.message.proto.SessionMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    boolean hasUserId();
    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    java.lang.String getUserId();
    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    com.google.protobuf.ByteString
        getUserIdBytes();

    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    boolean hasTimestamp();
    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    java.lang.String getTimestamp();
    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    com.google.protobuf.ByteString
        getTimestampBytes();

    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    boolean hasSignature();
    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    java.lang.String getSignature();
    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    com.google.protobuf.ByteString
        getSignatureBytes();
  }
  /**
   * Protobuf type {@code com.iauto.wlink.core.message.proto.SessionMessage}
   */
  public static final class SessionMessage extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.iauto.wlink.core.message.proto.SessionMessage)
      SessionMessageOrBuilder {
    // Use SessionMessage.newBuilder() to construct.
    private SessionMessage(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private SessionMessage(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final SessionMessage defaultInstance;
    public static SessionMessage getDefaultInstance() {
      return defaultInstance;
    }

    public SessionMessage getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private SessionMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000001;
              userId_ = bs;
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              timestamp_ = bs;
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              signature_ = bs;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.iauto.wlink.core.message.proto.SessionMessageProto.internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.iauto.wlink.core.message.proto.SessionMessageProto.internal_static_com_iauto_wlink_core_message_proto_SessionMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.class, com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.Builder.class);
    }

    public static com.google.protobuf.Parser<SessionMessage> PARSER =
        new com.google.protobuf.AbstractParser<SessionMessage>() {
      public SessionMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SessionMessage(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<SessionMessage> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    public static final int USERID_FIELD_NUMBER = 1;
    private java.lang.Object userId_;
    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    public boolean hasUserId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    public java.lang.String getUserId() {
      java.lang.Object ref = userId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          userId_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string userId = 1 [default = ""];</code>
     */
    public com.google.protobuf.ByteString
        getUserIdBytes() {
      java.lang.Object ref = userId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        userId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIMESTAMP_FIELD_NUMBER = 2;
    private java.lang.Object timestamp_;
    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    public boolean hasTimestamp() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    public java.lang.String getTimestamp() {
      java.lang.Object ref = timestamp_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          timestamp_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string timestamp = 2 [default = ""];</code>
     */
    public com.google.protobuf.ByteString
        getTimestampBytes() {
      java.lang.Object ref = timestamp_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        timestamp_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SIGNATURE_FIELD_NUMBER = 3;
    private java.lang.Object signature_;
    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    public boolean hasSignature() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    public java.lang.String getSignature() {
      java.lang.Object ref = signature_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          signature_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string signature = 3 [default = ""];</code>
     */
    public com.google.protobuf.ByteString
        getSignatureBytes() {
      java.lang.Object ref = signature_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        signature_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      userId_ = "";
      timestamp_ = "";
      signature_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasUserId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTimestamp()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasSignature()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getUserIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getTimestampBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getSignatureBytes());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getUserIdBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getTimestampBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getSignatureBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code com.iauto.wlink.core.message.proto.SessionMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.iauto.wlink.core.message.proto.SessionMessage)
        com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.iauto.wlink.core.message.proto.SessionMessageProto.internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.iauto.wlink.core.message.proto.SessionMessageProto.internal_static_com_iauto_wlink_core_message_proto_SessionMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.class, com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.Builder.class);
      }

      // Construct using com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        userId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        timestamp_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        signature_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.iauto.wlink.core.message.proto.SessionMessageProto.internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor;
      }

      public com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage getDefaultInstanceForType() {
        return com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.getDefaultInstance();
      }

      public com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage build() {
        com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage buildPartial() {
        com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage result = new com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.userId_ = userId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.timestamp_ = timestamp_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.signature_ = signature_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage) {
          return mergeFrom((com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage other) {
        if (other == com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage.getDefaultInstance()) return this;
        if (other.hasUserId()) {
          bitField0_ |= 0x00000001;
          userId_ = other.userId_;
          onChanged();
        }
        if (other.hasTimestamp()) {
          bitField0_ |= 0x00000002;
          timestamp_ = other.timestamp_;
          onChanged();
        }
        if (other.hasSignature()) {
          bitField0_ |= 0x00000004;
          signature_ = other.signature_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasUserId()) {
          
          return false;
        }
        if (!hasTimestamp()) {
          
          return false;
        }
        if (!hasSignature()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.iauto.wlink.core.message.proto.SessionMessageProto.SessionMessage) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object userId_ = "";
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public boolean hasUserId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public java.lang.String getUserId() {
        java.lang.Object ref = userId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            userId_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public com.google.protobuf.ByteString
          getUserIdBytes() {
        java.lang.Object ref = userId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          userId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public Builder setUserId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        userId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public Builder clearUserId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        userId_ = getDefaultInstance().getUserId();
        onChanged();
        return this;
      }
      /**
       * <code>required string userId = 1 [default = ""];</code>
       */
      public Builder setUserIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        userId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object timestamp_ = "";
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public boolean hasTimestamp() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public java.lang.String getTimestamp() {
        java.lang.Object ref = timestamp_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            timestamp_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public com.google.protobuf.ByteString
          getTimestampBytes() {
        java.lang.Object ref = timestamp_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          timestamp_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public Builder setTimestamp(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        timestamp_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public Builder clearTimestamp() {
        bitField0_ = (bitField0_ & ~0x00000002);
        timestamp_ = getDefaultInstance().getTimestamp();
        onChanged();
        return this;
      }
      /**
       * <code>required string timestamp = 2 [default = ""];</code>
       */
      public Builder setTimestampBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        timestamp_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object signature_ = "";
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public boolean hasSignature() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public java.lang.String getSignature() {
        java.lang.Object ref = signature_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            signature_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public com.google.protobuf.ByteString
          getSignatureBytes() {
        java.lang.Object ref = signature_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          signature_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public Builder setSignature(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        signature_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public Builder clearSignature() {
        bitField0_ = (bitField0_ & ~0x00000004);
        signature_ = getDefaultInstance().getSignature();
        onChanged();
        return this;
      }
      /**
       * <code>required string signature = 3 [default = ""];</code>
       */
      public Builder setSignatureBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        signature_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.iauto.wlink.core.message.proto.SessionMessage)
    }

    static {
      defaultInstance = new SessionMessage(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.iauto.wlink.core.message.proto.SessionMessage)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_iauto_wlink_core_message_proto_SessionMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037wlink/SessionMessageProto.proto\022\"com.i" +
      "auto.wlink.core.message.proto\"L\n\016Session" +
      "Message\022\020\n\006userId\030\001 \002(\t:\000\022\023\n\ttimestamp\030\002" +
      " \002(\t:\000\022\023\n\tsignature\030\003 \002(\t:\000"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_iauto_wlink_core_message_proto_SessionMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_iauto_wlink_core_message_proto_SessionMessage_descriptor,
        new java.lang.String[] { "UserId", "Timestamp", "Signature", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
