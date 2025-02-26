import pandas as pd
from sentence_transformers import SentenceTransformer, losses, InputExample
from torch.utils.data import DataLoader

# Step 1: Load Pretrained Model
model = SentenceTransformer("all-MiniLM-L6-v2")

# Step 2: Load Training Data
df = pd.read_csv("name_similarity.csv",delimiter="\t")
# Step 3: Convert Data into InputExamples
train_examples = [
    InputExample(texts=[row["Name1"], row["Name2"]], label=float(row["Similarity"]))
    for _, row in df.iterrows()
]

# Step 4: Create DataLoader
train_dataloader = DataLoader(train_examples, shuffle=True, batch_size=16)

# Step 5: Define Loss Function (Cosine Similarity Loss)
train_loss = losses.CosineSimilarityLoss(model)

# Step 6: Train the Model
print("Training started...")
model.fit(train_objectives=[(train_dataloader, train_loss)], epochs=3, warmup_steps=100)
print("Training completed!")

# Step 7: Save the Fine-Tuned Model
model.save("fine_tuned_name_similarity_model")
print("Model saved successfully!")
