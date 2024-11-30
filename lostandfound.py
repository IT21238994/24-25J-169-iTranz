"""LostandFound.ipynb

Automatically generated by Colab.

Original file is located at
    https://colab.research.google.com/drive/1AXQSAaNYY7quwNQarrOgu7E6cEkHxrxk
"""

# Step 1: Install necessary libraries
!pip install transformers datasets torch

# Step 2: Import required libraries
import pandas as pd
import torch
from sklearn.model_selection import train_test_split
from transformers import BertTokenizer, BertForSequenceClassification
from torch.utils.data import DataLoader, Dataset
from torch.optim import AdamW
from torch.nn import BCEWithLogitsLoss
from tqdm import tqdm

# Load dataset
file_path = '/content/drive/MyDrive/Lost_and_Found_Items.csv'
df = pd.read_csv(file_path)

# Assuming you have a DataFrame named 'df'
column_names = df.columns.tolist()

print(column_names)

#Step 3: Load the dataset
# Load the dataset from the mounted drive
file_path = '/content/drive/MyDrive/Lost_and_Found_Items.csv'  # Your dataset path
df = pd.read_csv(file_path)  # Load the dataset
df = df[['Lost Item Description', 'Found Item Description', 'Label']]  # Ensure these columns exist
df.head()

# Step 4: Split the dataset
train_texts, val_texts, train_labels, val_labels = train_test_split(
    df[['Lost Item Description', 'Found Item Description']].values, df['Label'].values, test_size=0.2, random_state=42
)

# Step 5: Tokenize using BERT tokenizer
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')

def tokenize(batch):
    return tokenizer(
        batch[:, 0].tolist(), batch[:, 1].tolist(),
        padding='max_length', truncation=True, max_length=128, return_tensors="pt"
    )

train_encodings = tokenize(train_texts)
val_encodings = tokenize(val_texts)

# Convert labels to tensors
train_labels = torch.tensor(train_labels)
val_labels = torch.tensor(val_labels)

# Step 6: Create Dataset class
class TextPairDataset(Dataset):
    def __init__(self, encodings, labels):
        self.encodings = encodings
        self.labels = labels

    def __len__(self):
        return len(self.labels)

    def __getitem__(self, idx):
        item = {key: val[idx] for key, val in self.encodings.items()}
        item['labels'] = self.labels[idx]
        return item

train_dataset = TextPairDataset(train_encodings, train_labels)
val_dataset = TextPairDataset(val_encodings, val_labels)

# Step 7: Define DataLoaders
train_loader = DataLoader(train_dataset, batch_size=16, shuffle=True)
val_loader = DataLoader(val_dataset, batch_size=16)

# Step 8: Load BERT model
model = BertForSequenceClassification.from_pretrained('bert-base-uncased', num_labels=1)  # Binary classification
model = model.to('cuda' if torch.cuda.is_available() else 'cpu')

# Step 9: Define optimizer and loss function
optimizer = AdamW(model.parameters(), lr=5e-5)
loss_fn = BCEWithLogitsLoss()

# Step 10: Training loop
def train_model(model, train_loader, val_loader, optimizer, loss_fn, epochs=3):
    for epoch in range(epochs):
        model.train()
        total_loss = 0
        for batch in tqdm(train_loader):
            batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
            optimizer.zero_grad()
            outputs = model(**batch)
            loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
            loss.backward()
            optimizer.step()
            total_loss += loss.item()
        avg_loss = total_loss / len(train_loader)
        print(f"Epoch {epoch + 1}, Loss: {avg_loss:.4f}")

        # Validation
        model.eval()
        val_loss = 0
        correct = 0
        total = 0
        with torch.no_grad():
            for batch in val_loader:
                batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
                outputs = model(**batch)
                loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
                val_loss += loss.item()
                predictions = torch.sigmoid(outputs.logits).round()
                correct += (predictions == batch['labels']).sum().item()
                total += batch['labels'].size(0)
        val_loss /= len(val_loader)
        accuracy = correct / total
        print(f"Validation Loss: {val_loss:.4f}, Accuracy: {accuracy:.4f}")

# Step 11: Train the model
train_model(model, train_loader, val_loader, optimizer, loss_fn, epochs=5)

# Step 12: Evaluate and visualize the model's performance
import matplotlib.pyplot as plt

def train_model_with_metrics(model, train_loader, val_loader, optimizer, loss_fn, epochs=3):
    train_accuracies = []
    val_accuracies = []
    train_losses = []
    val_losses = []

    for epoch in range(epochs):
        # Training phase
        model.train()
        total_loss = 0
        correct = 0
        total = 0
        for batch in tqdm(train_loader, desc=f"Training Epoch {epoch + 1}/{epochs}"):
            batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
            optimizer.zero_grad()
            outputs = model(**batch)
            loss = loss_fn(outputs.logits.squeeze(), batch['Labels'].float())
            loss.backward()
            optimizer.step()
            total_loss += loss.item()

            # Calculate training accuracy
            predictions = torch.sigmoid(outputs.logits).round()
            correct += (predictions == batch['Labels']).sum().item()
            total += batch['Labels'].size(0)

        train_accuracy = correct / total
        avg_train_loss = total_loss / len(train_loader)
        train_accuracies.append(train_accuracy)
        train_losses.append(avg_train_loss)

        print(f"Epoch {epoch + 1}, Train Loss: {avg_train_loss:.4f}, Train Accuracy: {train_accuracy:.4f}")

print(f"Epoch {epoch + 1}, Train Loss: {avg_train_loss:.4f}, Train Accuracy: {train_accuracy:.4f}")

# Validation phase
        model.eval()
        val_loss = 0
        correct = 0
        total = 0
        with torch.no_grad():
            for batch in tqdm(val_loader, desc=f"Validation Epoch {epoch + 1}/{epochs}"):
                batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
                outputs = model(**batch)
                loss = loss_fn(outputs.logits.squeeze(), batch['Labels'].float())
                val_loss += loss.item()
                predictions = torch.sigmoid(outputs.logits).round()
                correct += (predictions == batch['Labels']).sum().item()
                total += batch['Labels'].size(0)

        val_accuracy = correct / total
        avg_val_loss = val_loss / len(val_loader)
        val_accuracies.append(val_accuracy)
        val_losses.append(avg_val_loss)

        print(f"Epoch {epoch + 1}, Validation Loss: {avg_val_loss:.4f}, Validation Accuracy: {val_accuracy:.4f}")

    return train_accuracies, val_accuracies, train_losses, val_losses

# Train and track metrics
train_accuracies, val_accuracies, train_losses, val_losses = train_model_with_metrics(
    model, train_loader, val_loader, optimizer, loss_fn, epochs=5
)

# Plotting Accuracy and Loss
plt.figure(figsize=(12, 5))

# Plot Accuracy
plt.subplot(1, 2, 1)
plt.plot(range(1, len(train_accuracies) + 1), train_accuracies, label='Training Accuracy')
plt.plot(range(1, len(val_accuracies) + 1), val_accuracies, label='Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.title('Training and Validation Accuracy')
plt.legend()

# Plot Loss
plt.subplot(1, 2, 2)
plt.plot(range(1, len(train_losses) + 1), train_losses, label='Training Loss')
plt.plot(range(1, len(val_losses) + 1), val_losses, label='Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.title('Training and Validation Loss')
plt.legend()

plt.show()

# Step 12: Evaluate and visualize the model's performance
import matplotlib.pyplot as plt

def train_model_with_metrics(model, train_loader, val_loader, optimizer, loss_fn, epochs=3):
    train_accuracies = []
    val_accuracies = []
    train_losses = []
    val_losses = []

    for epoch in range(epochs):
        # Training phase
        model.train()
        total_loss = 0
        correct = 0
        total = 0
        for batch in tqdm(train_loader, desc=f"Training Epoch {epoch + 1}/{epochs}"):
            batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
            optimizer.zero_grad()
            outputs = model(**batch)
            loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
            loss.backward()
            optimizer.step()
            total_loss += loss.item()

            # Calculate training accuracy
            predictions = torch.sigmoid(outputs.logits).round()
            correct += (predictions == batch['labels']).sum().item()
            total += batch['labels'].size(0)

        train_accuracy = correct / total
        avg_train_loss = total_loss / len(train_loader)
        train_accuracies.append(train_accuracy)
        train_losses.append(avg_train_loss)

        print(f"Epoch {epoch + 1}, Train Loss: {avg_train_loss:.4f}, Train Accuracy: {train_accuracy:.4f}")

        # Validation phase
        model.eval()
        val_loss = 0
        correct = 0
        total = 0
        with torch.no_grad():
            for batch in tqdm(val_loader, desc=f"Validation Epoch {epoch + 1}/{epochs}"):
                batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
                outputs = model(**batch)
                loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
                val_loss += loss.item()
                predictions = torch.sigmoid(outputs.logits).round()
                correct += (predictions == batch['labels']).sum().item()
                total += batch['labels'].size(0)

        val_accuracy = correct / total
        avg_val_loss = val_loss / len(val_loader)
        val_accuracies.append(val_accuracy)
        val_losses.append(avg_val_loss)

        print(f"Epoch {epoch + 1}, Validation Loss: {avg_val_loss:.4f}, Validation Accuracy: {val_accuracy:.4f}")

    return train_accuracies, val_accuracies, train_losses, val_losses

# Train and track metrics
train_accuracies, val_accuracies, train_losses, val_losses = train_model_with_metrics(
    model, train_loader, val_loader, optimizer, loss_fn, epochs=5
)

# Plotting Accuracy and Loss
plt.figure(figsize=(12, 5))

# Plot Accuracy
plt.subplot(1, 2, 1)
plt.plot(range(1, len(train_accuracies) + 1), train_accuracies, label='Training Accuracy')
plt.plot(range(1, len(val_accuracies) + 1), val_accuracies, label='Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.title('Training and Validation Accuracy')
plt.legend()

# Plot Loss
plt.subplot(1, 2, 2)
plt.plot(range(1, len(train_losses) + 1), train_losses, label='Training Loss')
plt.plot(range(1, len(val_losses) + 1), val_losses, label='Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.title('Training and Validation Loss')
plt.legend()

plt.show()

# Step 12: Evaluate and visualize the model's performance
import matplotlib.pyplot as plt

def train_model_with_metrics(model, train_loader, val_loader, optimizer, loss_fn, epochs=25):
    train_accuracies = []
    val_accuracies = []
    train_losses = []
    val_losses = []

    for epoch in range(epochs):
        # Training phase
        model.train()
        total_loss = 0
        correct = 0
        total = 0
        for batch in tqdm(train_loader, desc=f"Training Epoch {epoch + 1}/{epochs}"):
            batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
            optimizer.zero_grad()
            outputs = model(**batch)
            loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
            loss.backward()
            optimizer.step()
            total_loss += loss.item()

            # Calculate training accuracy
            predictions = torch.sigmoid(outputs.logits).round()
            correct += (predictions == batch['labels']).sum().item()
            total += batch['labels'].size(0)

        train_accuracy = correct / total
        avg_train_loss = total_loss / len(train_loader)
        train_accuracies.append(train_accuracy)
        train_losses.append(avg_train_loss)

        print(f"Epoch {epoch + 1}, Train Loss: {avg_train_loss:.4f}, Train Accuracy: {train_accuracy:.4f}")

        # Validation phase
        model.eval()
        val_loss = 0
        correct = 0
        total = 0
        with torch.no_grad():
            for batch in tqdm(val_loader, desc=f"Validation Epoch {epoch + 1}/{epochs}"):
                batch = {k: v.to('cuda' if torch.cuda.is_available() else 'cpu') for k, v in batch.items()}
                outputs = model(**batch)
                loss = loss_fn(outputs.logits.squeeze(), batch['labels'].float())
                val_loss += loss.item()
                predictions = torch.sigmoid(outputs.logits).round()
                correct += (predictions == batch['labels']).sum().item()
                total += batch['labels'].size(0)

        val_accuracy = correct / total
        avg_val_loss = val_loss / len(val_loader)
        val_accuracies.append(val_accuracy)
        val_losses.append(avg_val_loss)

        print(f"Epoch {epoch + 1}, Validation Loss: {avg_val_loss:.4f}, Validation Accuracy: {val_accuracy:.4f}")

    return train_accuracies, val_accuracies, train_losses, val_losses

# Train and track metrics
train_accuracies, val_accuracies, train_losses, val_losses = train_model_with_metrics(
    model, train_loader, val_loader, optimizer, loss_fn, epochs=5
)

# Plotting Accuracy and Loss
plt.figure(figsize=(12, 5))

# Plot Accuracy
plt.subplot(1, 2, 1)
plt.plot(range(1, len(train_accuracies) + 1), train_accuracies, label='Training Accuracy')
plt.plot(range(1, len(val_accuracies) + 1), val_accuracies, label='Validation Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.title('Training and Validation Accuracy')
plt.legend()

# Plot Loss
plt.subplot(1, 2, 2)
plt.plot(range(1, len(train_losses) + 1), train_losses, label='Training Loss')
plt.plot(range(1, len(val_losses) + 1), val_losses, label='Validation Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.title('Training and Validation Loss')
plt.legend()

plt.show()

# Step 11: Train the model
train_model(model, train_loader, val_loader, optimizer, loss_fn, epochs=25)