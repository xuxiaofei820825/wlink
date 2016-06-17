// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: wlink/MessageAcknowledgeProto.proto

package com.iauto.wlink.core.message.proto;

public final class MessageAcknowledgeProto {
  private MessageAcknowledgeProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface MessageAcknowledgeOrBuilder extends
      // @@protoc_insertion_point(interface_extends:com.iauto.wlink.core.message.proto.MessageAcknowledge)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
     */
    boolean hasResult();
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
     */
    com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result getResult();

    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
     */
    boolean hasAckType();
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
     */
    com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType getAckType();

    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    boolean hasMessageId();
    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    java.lang.String getMessageId();
    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    com.google.protobuf.ByteString
        getMessageIdBytes();
  }
  /**
   * Protobuf type {@code com.iauto.wlink.core.message.proto.MessageAcknowledge}
   */
  public static final class MessageAcknowledge extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:com.iauto.wlink.core.message.proto.MessageAcknowledge)
      MessageAcknowledgeOrBuilder {
    // Use MessageAcknowledge.newBuilder() to construct.
    private MessageAcknowledge(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private MessageAcknowledge(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final MessageAcknowledge defaultInstance;
    public static MessageAcknowledge getDefaultInstance() {
      return defaultInstance;
    }

    public MessageAcknowledge getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private MessageAcknowledge(
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
            case 8: {
              int rawValue = input.readEnum();
              com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result value = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                result_ = value;
              }
              break;
            }
            case 16: {
              int rawValue = input.readEnum();
              com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType value = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(2, rawValue);
              } else {
                bitField0_ |= 0x00000002;
                ackType_ = value;
              }
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              messageId_ = bs;
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
      return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.class, com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Builder.class);
    }

    public static com.google.protobuf.Parser<MessageAcknowledge> PARSER =
        new com.google.protobuf.AbstractParser<MessageAcknowledge>() {
      public MessageAcknowledge parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new MessageAcknowledge(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<MessageAcknowledge> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code com.iauto.wlink.core.message.proto.MessageAcknowledge.Result}
     */
    public enum Result
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>SUCCESS = 1;</code>
       */
      SUCCESS(0, 1),
      /**
       * <code>FAILURE = 0;</code>
       */
      FAILURE(1, 0),
      ;

      /**
       * <code>SUCCESS = 1;</code>
       */
      public static final int SUCCESS_VALUE = 1;
      /**
       * <code>FAILURE = 0;</code>
       */
      public static final int FAILURE_VALUE = 0;


      public final int getNumber() { return value; }

      public static Result valueOf(int value) {
        switch (value) {
          case 1: return SUCCESS;
          case 0: return FAILURE;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Result>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<Result>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Result>() {
              public Result findValueByNumber(int number) {
                return Result.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.getDescriptor().getEnumTypes().get(0);
      }

      private static final Result[] VALUES = values();

      public static Result valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private Result(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:com.iauto.wlink.core.message.proto.MessageAcknowledge.Result)
    }

    /**
     * Protobuf enum {@code com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType}
     */
    public enum AckType
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>SEND = 1;</code>
       */
      SEND(0, 1),
      /**
       * <code>RECEIVE = 0;</code>
       */
      RECEIVE(1, 0),
      ;

      /**
       * <code>SEND = 1;</code>
       */
      public static final int SEND_VALUE = 1;
      /**
       * <code>RECEIVE = 0;</code>
       */
      public static final int RECEIVE_VALUE = 0;


      public final int getNumber() { return value; }

      public static AckType valueOf(int value) {
        switch (value) {
          case 1: return SEND;
          case 0: return RECEIVE;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<AckType>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<AckType>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<AckType>() {
              public AckType findValueByNumber(int number) {
                return AckType.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.getDescriptor().getEnumTypes().get(1);
      }

      private static final AckType[] VALUES = values();

      public static AckType valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new java.lang.IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private AckType(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType)
    }

    private int bitField0_;
    public static final int RESULT_FIELD_NUMBER = 1;
    private com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result result_;
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
     */
    public boolean hasResult() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
     */
    public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result getResult() {
      return result_;
    }

    public static final int ACKTYPE_FIELD_NUMBER = 2;
    private com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType ackType_;
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
     */
    public boolean hasAckType() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
     */
    public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType getAckType() {
      return ackType_;
    }

    public static final int MESSAGEID_FIELD_NUMBER = 3;
    private java.lang.Object messageId_;
    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    public boolean hasMessageId() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    public java.lang.String getMessageId() {
      java.lang.Object ref = messageId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          messageId_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string messageId = 3 [default = ""];</code>
     */
    public com.google.protobuf.ByteString
        getMessageIdBytes() {
      java.lang.Object ref = messageId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        messageId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      result_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result.FAILURE;
      ackType_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType.SEND;
      messageId_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasResult()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasAckType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasMessageId()) {
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
        output.writeEnum(1, result_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeEnum(2, ackType_.getNumber());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getMessageIdBytes());
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
          .computeEnumSize(1, result_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(2, ackType_.getNumber());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getMessageIdBytes());
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

    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge prototype) {
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
     * Protobuf type {@code com.iauto.wlink.core.message.proto.MessageAcknowledge}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:com.iauto.wlink.core.message.proto.MessageAcknowledge)
        com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledgeOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.class, com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Builder.class);
      }

      // Construct using com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.newBuilder()
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
        result_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result.FAILURE;
        bitField0_ = (bitField0_ & ~0x00000001);
        ackType_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType.SEND;
        bitField0_ = (bitField0_ & ~0x00000002);
        messageId_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor;
      }

      public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge getDefaultInstanceForType() {
        return com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.getDefaultInstance();
      }

      public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge build() {
        com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge buildPartial() {
        com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge result = new com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.result_ = result_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.ackType_ = ackType_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.messageId_ = messageId_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge) {
          return mergeFrom((com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge other) {
        if (other == com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.getDefaultInstance()) return this;
        if (other.hasResult()) {
          setResult(other.getResult());
        }
        if (other.hasAckType()) {
          setAckType(other.getAckType());
        }
        if (other.hasMessageId()) {
          bitField0_ |= 0x00000004;
          messageId_ = other.messageId_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasResult()) {
          
          return false;
        }
        if (!hasAckType()) {
          
          return false;
        }
        if (!hasMessageId()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result result_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result.FAILURE;
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
       */
      public boolean hasResult() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
       */
      public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result getResult() {
        return result_;
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
       */
      public Builder setResult(com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        result_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.Result result = 1 [default = FAILURE];</code>
       */
      public Builder clearResult() {
        bitField0_ = (bitField0_ & ~0x00000001);
        result_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.Result.FAILURE;
        onChanged();
        return this;
      }

      private com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType ackType_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType.SEND;
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
       */
      public boolean hasAckType() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
       */
      public com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType getAckType() {
        return ackType_;
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
       */
      public Builder setAckType(com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000002;
        ackType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .com.iauto.wlink.core.message.proto.MessageAcknowledge.AckType ackType = 2 [default = SEND];</code>
       */
      public Builder clearAckType() {
        bitField0_ = (bitField0_ & ~0x00000002);
        ackType_ = com.iauto.wlink.core.message.proto.MessageAcknowledgeProto.MessageAcknowledge.AckType.SEND;
        onChanged();
        return this;
      }

      private java.lang.Object messageId_ = "";
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public boolean hasMessageId() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public java.lang.String getMessageId() {
        java.lang.Object ref = messageId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            messageId_ = s;
          }
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public com.google.protobuf.ByteString
          getMessageIdBytes() {
        java.lang.Object ref = messageId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          messageId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public Builder setMessageId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        messageId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public Builder clearMessageId() {
        bitField0_ = (bitField0_ & ~0x00000004);
        messageId_ = getDefaultInstance().getMessageId();
        onChanged();
        return this;
      }
      /**
       * <code>required string messageId = 3 [default = ""];</code>
       */
      public Builder setMessageIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        messageId_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:com.iauto.wlink.core.message.proto.MessageAcknowledge)
    }

    static {
      defaultInstance = new MessageAcknowledge(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:com.iauto.wlink.core.message.proto.MessageAcknowledge)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n#wlink/MessageAcknowledgeProto.proto\022\"c" +
      "om.iauto.wlink.core.message.proto\"\236\002\n\022Me" +
      "ssageAcknowledge\022V\n\006result\030\001 \002(\0162=.com.i" +
      "auto.wlink.core.message.proto.MessageAck" +
      "nowledge.Result:\007FAILURE\022U\n\007ackType\030\002 \002(" +
      "\0162>.com.iauto.wlink.core.message.proto.M" +
      "essageAcknowledge.AckType:\004SEND\022\023\n\tmessa" +
      "geId\030\003 \002(\t:\000\"\"\n\006Result\022\013\n\007SUCCESS\020\001\022\013\n\007F" +
      "AILURE\020\000\" \n\007AckType\022\010\n\004SEND\020\001\022\013\n\007RECEIVE" +
      "\020\000"
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
    internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_com_iauto_wlink_core_message_proto_MessageAcknowledge_descriptor,
        new java.lang.String[] { "Result", "AckType", "MessageId", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
