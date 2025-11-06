package com.example.fashionshopapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fashionshopapp.R;
import com.example.fashionshopapp.model.AiChatMessage;
import java.util.List;

public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.MessageViewHolder> {

    private final List<AiChatMessage> messageList;

    public AiChatAdapter(List<AiChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getMessageType();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == AiChatMessage.TYPE_USER) {
            view = inflater.inflate(R.layout.item_ai_chat_user, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_ai_chat_bot, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        AiChatMessage message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_content);
        }
    }
}
