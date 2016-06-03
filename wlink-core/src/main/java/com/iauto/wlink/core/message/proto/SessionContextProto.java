// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wlink/SessionContextProto.proto

package com.iauto.wlink.core.message.proto;

public final class SessionContextProto {
  private SessionContextProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface SessionContextOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.iauto.wlink.core.message.proto.SessionContext)
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
     * <code>required bytes signature = 3;</code>
     */
    boolean hasSignature();
    /**
     * <code>required bytes signature = 3;</code>
     */
    com.google.protobuf.ByteString getSignature();
  }
  /**
   * Protobuf type {@code com.iauto.wlink.core.message.proto.SessionContext}
   */
  public static final class SessionContext extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.iauto.wlink.core.message.proto.SessionContext)
      SessionContextOrBuilder {
    // Use SessionContext.newBuilder() to construct.
    private SessionContext(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private SessionContext(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final SessionContext defaultInstance;
    public static SessionContext getDefaultInstance() {
      return defaultInstance;
    }

    public SessionContext getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private SessionContext(
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
              bitField0_ |= 0x00000004;
              signature_ = input.readBytes();
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
      return com.iauto.wlink.core.message.proto.SessionContextProto.internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.iauto.wlink.core.message.proto.SessionContextProto.internal_static_com_iauto_wlink_core_message_proto_SessionContext_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.class, com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.Builder.class);
    }

    public static com.google.protobuf.Parser<SessionContext> PARSER =
        new com.google.protobuf.AbstractParser<SessionContext>() {
      public SessionContext parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SessionContext(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<SessionContext> getParserForType() {
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
    private com.google.protobuf.ByteString signature_;
    /**
     * <code>required bytes signature = 3;</code>
     */
    public boolean hasSignature() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required bytes signature = 3;</code>
     */
    public com.google.protobuf.ByteString getSignature() {
      return signature_;
    }

    private void initFields() {
      userId_ = "";
      timestamp_ = "";
      signature_ = com.google.protobuf.ByteString.EMPTY;
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
        output.writeBytes(3, signature_);
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
          .computeBytesSize(3, signature_);
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

    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext prototype) {
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
     * Protobuf type {@code com.iauto.wlink.core.message.proto.SessionContext}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.iauto.wlink.core.message.proto.SessionContext)
        com.iauto.wlink.core.message.proto.SessionContextProto.SessionContextOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.iauto.wlink.core.message.proto.SessionContextProto.internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.iauto.wlink.core.message.proto.SessionContextProto.internal_static_com_iauto_wlink_core_message_proto_SessionContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.class, com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.Builder.class);
      }

      // Construct using com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.newBuilder()
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
        signature_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.iauto.wlink.core.message.proto.SessionContextProto.internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor;
      }

      public com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext getDefaultInstanceForType() {
        return com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.getDefaultInstance();
      }

      public com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext build() {
        com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext buildPartial() {
        com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext result = new com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext(this);
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
        if (other instanceof com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext) {
          return mergeFrom((com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext other) {
        if (other == com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext.getDefaultInstance()) return this;
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
          setSignature(other.getSignature());
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
        com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.iauto.wlink.core.message.proto.SessionContextProto.SessionContext) e.getUnfinishedMessage();
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

      private com.google.protobuf.ByteString signature_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes signature = 3;</code>
       */
      public boolean hasSignature() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required bytes signature = 3;</code>
       */
      public com.google.protobuf.ByteString getSignature() {
        return signature_;
      }
      /**
       * <code>required bytes signature = 3;</code>
       */
      public Builder setSignature(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        signature_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes signature = 3;</code>
       */
      public Builder clearSignature() {
        bitField0_ = (bitField0_ & ~0x00000004);
        signature_ = getDefaultInstance().getSignature();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.iauto.wlink.core.message.proto.SessionContext)
    }

    static {
      defaultInstance = new SessionContext(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.iauto.wlink.core.message.proto.SessionContext)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_iauto_wlink_core_message_proto_SessionContext_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\037wlink/SessionContextProto.proto\022\"com.i" +
      "auto.wlink.core.message.proto\"J\n\016Session" +
      "Context\022\020\n\006userId\030\001 \002(\t:\000\022\023\n\ttimestamp\030\002" +
      " \002(\t:\000\022\021\n\tsignature\030\003 \002(\014"
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
    internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_iauto_wlink_core_message_proto_SessionContext_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_iauto_wlink_core_message_proto_SessionContext_descriptor,
        new java.lang.String[] { "UserId", "Timestamp", "Signature", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
