package com.example.guardify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guardify.ui.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final Context context;
    private final List<Message> messageList;
    private final String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        String formattedTime = "";
        if (message.getTimestamp() != null) {
            formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(message.getTimestamp().toDate());
        }

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.tvMessage.setText(message.getText());
            sentHolder.tvTime.setText(formattedTime);
            sentHolder.tvSenderName.setText("You");

            if ("read".equalsIgnoreCase(message.getStatus())) {
                sentHolder.imgStatusTick.setImageResource(R.drawable.ic_double_tick);
                sentHolder.imgStatusTick.setColorFilter(context.getColor(R.color.white));
            } else {
                sentHolder.imgStatusTick.setImageResource(R.drawable.ic_single_tick);
                sentHolder.imgStatusTick.setColorFilter(context.getColor(R.color.white));
            }

        } else if (holder instanceof ReceivedViewHolder) {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            receivedHolder.tvMessage.setText(message.getText());
            receivedHolder.tvTime.setText(formattedTime);

            String name = message.getSenderName();
            if (name == null || name.trim().isEmpty()) {
                name = "Teammate"; // fallback
            }
            receivedHolder.tvSenderName.setText(name);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSenderName;
        ImageView imgStatusTick;

        SentViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvSentMessage);
            tvTime = view.findViewById(R.id.tvSentTime);
            tvSenderName = view.findViewById(R.id.tvSentSenderName);
            imgStatusTick = view.findViewById(R.id.imgTick);
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime, tvSenderName;

        ReceivedViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvReceivedMessage);
            tvTime = view.findViewById(R.id.tvReceivedTime);
            tvSenderName = view.findViewById(R.id.tvReceivedSenderName);
        }
    }
}
