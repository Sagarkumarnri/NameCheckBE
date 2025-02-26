from fastapi import FastAPI
from sentence_transformers import SentenceTransformer
from pydantic import BaseModel
import numpy as np
import uvicorn

# 🔥 Load the fine-tuned model from the saved directory
model = SentenceTransformer("fine_tuned_name_similarity_model")

app = FastAPI()

# Define request schema
class NamePair(BaseModel):
    name1: str
    name2: str

@app.post("/similarity")
def get_similarity(request: NamePair):
    # Convert names to embeddings
    emb1 = model.encode(request.name1)
    emb2 = model.encode(request.name2)

    # Compute Cosine Similarity
    similarity = np.dot(emb1, emb2) / (np.linalg.norm(emb1) * np.linalg.norm(emb2))

    return {"similarity_percentage": round(float(similarity) * 100, 2)}

# Run API
if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
