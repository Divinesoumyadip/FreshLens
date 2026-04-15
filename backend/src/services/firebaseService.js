const { db, storage } = require('../config/firebase');
const { COLLECTIONS } = require('../config/constants');

// Firestore helpers
const getDoc = async (collection, id) => {
  const snap = await db.collection(collection).doc(id).get();
  return snap.exists ? { id: snap.id, ...snap.data() } : null;
};

const setDoc = async (collection, id, data) => {
  await db.collection(collection).doc(id).set({ ...data, updatedAt: new Date() }, { merge: true });
};

const addDoc = async (collection, data) => {
  const ref = await db.collection(collection).add({ ...data, createdAt: new Date() });
  return ref.id;
};

const queryDocs = async (collection, filters = [], limit = 50) => {
  let ref = db.collection(collection);
  filters.forEach(([field, op, value]) => { ref = ref.where(field, op, value); });
  const snap = await ref.limit(limit).get();
  return snap.docs.map(d => ({ id: d.id, ...d.data() }));
};

// Storage: upload image
const uploadImage = async (buffer, filename, mimetype) => {
  const bucket = storage.bucket();
  const file = bucket.file(`reports/${filename}`);
  await file.save(buffer, { contentType: mimetype, public: true });
  return `https://storage.googleapis.com/${bucket.name}/reports/${filename}`;
};

module.exports = { getDoc, setDoc, addDoc, queryDocs, uploadImage };
